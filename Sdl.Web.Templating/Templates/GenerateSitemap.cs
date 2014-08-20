using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Web.Script.Serialization;
using System.Xml;
using System.Xml.Linq;
using Sdl.Web.Tridion.Common;
using Tridion.ContentManager;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Publishing;
using Tridion.ContentManager.Templating.Assembly;
using tpl = Tridion.ContentManager.Templating;

namespace Sdl.Web.Tridion.Templates
{
    /// <summary>
    /// Generates sitemap JSON. 
    /// Should be used in a Component Template.
    /// </summary>
    [TcmTemplateTitle("Generate Sitemap")]
    public class GenerateSiteMap : TemplateBase
    {
        private const string MainRegionName = "Main";

        private StructureGroup _startPoint;
        private NavigationConfig _config;

        public override void Transform(tpl.Engine engine, tpl.Package package)
        {
            Initialize(engine, package);
            _config = LoadConfig(GetComponent());
            _startPoint = GetPublication().RootStructureGroup;

            if (_startPoint != null)
            {
                string nav = GenerateNavigation();
                if (!string.IsNullOrEmpty(GenerateNavigation()))
                {
                    package.PushItem(tpl.Package.OutputName, package.CreateStringItem(tpl.ContentType.Text, nav));
                }
            }
        }

        private static NavigationConfig LoadConfig(Component component)
        {
            NavigationConfig res = new NavigationConfig {NavType = NavigationType.Simple};
            if (component.Metadata != null)
            {
                var meta = new ItemFields(component.Metadata, component.MetadataSchema);
                var type = meta.GetKeywordValue("navigationType");
                switch (type.Key.ToLower())
                {
                    case "localizable":
                        res.NavType = NavigationType.Localizable;
                        break;
                }
                var navTextFields = meta.GetSingleFieldValue("navigationTextFieldPaths");
                if (!String.IsNullOrEmpty(navTextFields))
                {
                    res.NavTextFieldPaths = navTextFields.Split(',').Select(s => s.Trim()).ToList();
                }
                res.ExternalUrlTemplate = meta.GetSingleFieldValue("externalLinkTemplate");
            }
            return res;
        }

        public string GenerateNavigation()
        {
            try
            {
                return new JavaScriptSerializer().Serialize(GenerateStructureGroupNavigation(_startPoint));
            }
            catch (Exception ex)
            {
                Logger.Error(ex.Message);
                throw;
            }
        }

        private SitemapItem GenerateStructureGroupNavigation(StructureGroup startPoint)
        {
            var root = GenerateFolderNode(startPoint);
            var orderedDocument = GetItemsInFolderAsAList(startPoint);

            foreach (XElement pgNode in orderedDocument)
            {
                Page page = Engine.GetObject(pgNode.Attribute("ID").Value) as Page;
                if (page != null)
                {
                    if (IsPublished(page) && !IsSystem(pgNode.Attribute("Title").Value))
                    {
                        root.Items.Add(GeneratePageNode(page));
                    }
                }
                else
                {
                    if (!IsSystem(pgNode.Attribute("Title").Value))
                    {
                        root.Items.Add(GenerateStructureGroupNavigation(Engine.GetObject(pgNode.Attribute("ID").Value) as StructureGroup));
                    }
                }

            }
            return root;
        }

        private SitemapItem GenerateFolderNode(StructureGroup startPoint)
        {
            SitemapItem root = new SitemapItem(GetNavigationTitle(startPoint))
                {
                    Id = startPoint.Id,
                    Url = GetUrl(startPoint),
                    Type = ItemType.StructureGroup.ToString(),
                    Visible = IsVisible(startPoint.Title)
                };

            return root;
        }

        protected string GetNavigationTitle(StructureGroup sg)
        {
            string result = StripPrefix(sg.Title);
            return result;
        }

        protected string StripPrefix(string title)
        {
            return Regex.Replace(title, @"^\d{3}\s", String.Empty);
        }

        private static string GetUrl(StructureGroup sg)
        {
            String url = sg.PublishLocationUrl;
            return System.Web.HttpUtility.UrlDecode(url);
        }

        private SitemapItem GeneratePageNode(Page page)
        {
            SitemapItem sitemapItem = new SitemapItem(GetNavigationTitle(page))
            {
                Id = page.Id,
                Url = GetUrl(page),
                Type = ItemType.Page.ToString(),
                PublishedDate = GetPublishedDate(page, Engine.PublishingContext.PublicationTarget),
                Visible = IsVisible(page.Title)
            };
            return sitemapItem;
        }

        private static string GetPublishedDate(Page page, PublicationTarget target )
        {
            var publishInfos = PublishEngine.GetPublishInfo(page);
            foreach (var publishInfo in publishInfos)
            {
                if (publishInfo.PublicationTarget == target)
                {
                    return publishInfo.PublishedAt.GetIso8601Date();
                }
            }
            return "";
        }

        private string GetNavigationTitle(Page page)
        {
            string title = null;
            if (_config.NavType == NavigationType.Localizable)
            {
                title = GetNavTextFromPageComponents(page);
            }
            return String.IsNullOrEmpty(title) ? StripPrefix(page.Title) : title;
        }

        private string GetNavTextFromPageComponents(Page page)
        {
            string title = null;
            foreach (var cp in page.ComponentPresentations)
            {
                title = GetNavTitleFromComponent(cp.Component);
                if (!String.IsNullOrEmpty(title))
                {
                    return title;
                }
            }
            return title;
        }

        private string GetNavTitleFromComponent(Component component)
        {
            List<XmlElement> data = new List<XmlElement>();
            if (component.Content != null)
            {
                data.Add(component.Content);
            }
            if (component.Metadata != null)
            {
                data.Add(component.Metadata);
            }
            //var meta = component.Metadata;
            //var content = component.Content;
            foreach (var fieldname in _config.NavTextFieldPaths)
            {
                var title = GetNavTitleFromField(fieldname, data);
                if (!String.IsNullOrEmpty(title))
                {
                    return title;
                }
            }
            return null;
        }

        private static string GetNavTitleFromField(string fieldname, IEnumerable<XmlElement> data)
        {
            string xpath = GetXPathFromFieldName(fieldname);
            foreach (var fieldData in data)
            {
                var field = fieldData.SelectSingleNode(xpath);
                if (field != null)
                {
                    return field.InnerText;
                }
            }
            return null;
        }

        private static string GetXPathFromFieldName(string fieldname)
        {
            var bits = fieldname.Split('/');
            return "//" + String.Join("/", bits.Select(f => String.Format("*[local-name()='{0}']", f)));
        }

        private string GetRegionFromComponentPresentation(ComponentPresentation cp)
        {
            var match = Regex.Match(cp.ComponentTemplate.Title, @".*?\[(.*?)\]");
            if (match.Success)
            {
                return match.Groups[1].Value;
            }
            //default region name
            return MainRegionName;
        }

        private string GetNavigationTitleFromComp(Component mainComp)
        {
            //TODO, make the field names used to extract the title configurable as TBB parameters
            string title = null;
            if (mainComp != null)
            {
                if (mainComp.Metadata != null)
                {
                    ItemFields meta = new ItemFields(mainComp.Metadata, mainComp.MetadataSchema);
                    var embedMeta = meta.GetEmbeddedField("standardMeta");
                    if (embedMeta != null)
                    {
                        title = embedMeta.GetTextValue("name");
                    }
                }
                if (String.IsNullOrEmpty(title))
                {
                    ItemFields content = new ItemFields(mainComp.Content, mainComp.Schema);
                    title = content.GetTextValue("headline");
                }
            }
            return title;
        }

        protected string GetUrl(Page page)
        {
            String url = page.PublishLocationUrl;
            if (_config.ExternalUrlTemplate.ToLower() == page.PageTemplate.Title.ToLower() && page.Metadata != null)
            {
                var meta = new ItemFields(page.Metadata,page.MetadataSchema);
                var link = meta.GetEmbeddedField("redirect");
                url = link.GetExternalLink("externalLink");
                if (String.IsNullOrEmpty(url))
                {
                    url = link.GetSingleFieldValue("internalLink");
                }
            }
            return System.Web.HttpUtility.UrlDecode(url);
        }

        private IEnumerable<XElement> GetItemsInFolderAsAList(StructureGroup startPoint)
        {
            var filter = new OrganizationalItemItemsFilter(Engine.GetSession())
                {
                    ItemTypes = new List<ItemType> {ItemType.Page, ItemType.StructureGroup},
                    BaseColumns = ListBaseColumns.Extended
                };
            //get pages first to see if they have to appear in nav 
            XmlElement pagesXml = startPoint.GetListItems(filter);
            XmlDocument rootDocument = new XmlDocument();

            rootDocument.LoadXml(pagesXml.OuterXml);
            XDocument pageDoc = XDocument.Parse(rootDocument.OuterXml);

            var orderedDocument = (from XElement el in pageDoc.Root.Descendants()
                                   orderby el.Attribute("Title").Value
                                   select el).ToList();
            return orderedDocument;
        }

        private bool IsPublished(Page page)
        {
            if (Engine.PublishingContext.PublicationTarget != null)
            {
                return PublishEngine.IsPublished(page, Engine.PublishingContext.PublicationTarget);
            }
            //For preview we always return true - to help debugging
            return true;
        }

        private static bool IsVisible(string title)
        {
            Match match = Regex.Match(title, @"^\d{3}\s");
            return match.Success;
        }

        private static bool IsSystem(string title)
        {
            return title.StartsWith("_");
        }
    }

    internal enum NavigationType
    {
        Simple,
        Localizable
    }

    #region Sitemap Classes
    internal class NavigationConfig
    {
        public List<string> NavTextFieldPaths { get; set; }
        public NavigationType NavType { get; set; }
        public string ExternalUrlTemplate { get; set; }
    }

    internal class SitemapItem
    {
        private string _url;

        public SitemapItem()
        {
            Items = new List<SitemapItem>();
        }

        public SitemapItem(String title)
        {
            Items = new List<SitemapItem>();
            Title = title;
        }

        public string Title { get; set; }

        public string Url
        {
            get { return _url; }
            set { _url = RemoveNonRequiredExtensions(value); }
        }

        private string RemoveNonRequiredExtensions(string value)
        {
            //TODO make this configurable with TBB parameters
            return value.Replace(".html","");
        }

        public string Id { get; set; }
        public string Type { get; set; }
        public List<SitemapItem> Items { get; set; }
        public string PublishedDate { get; set; }
        public bool Visible { get; set; }
    }
    #endregion Sitemap Classes
}


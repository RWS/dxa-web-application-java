using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Web.Script.Serialization;
using System.Xml;
using System.Xml.Linq;
using Tridion.ContentManager;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Publishing;
using tpl = Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;
using Sdl.Web.Tridion.Common;

namespace Sdl.Web.Tridion.Templates
{
    /// <summary>
    /// Generates sitemap JSON. Should be used in a page template
    /// </summary>
    [TcmTemplateTitle("Generate Sitemap")]
    public class GenerateSiteMap : TemplateBase
    {
        private StructureGroup _startPoint;
        private const string MainRegionName = "Main";

        public override void Transform(tpl.Engine engine, tpl.Package package)
        {
            Initialize(engine, package);
            if (GetPage() != null)
            {
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
            //TODO: Logic can be included here to be able to add external urls
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

        private static DateTime GetPublishedDate(Page page, PublicationTarget target )
        {
            var publishInfos = PublishEngine.GetPublishInfo(page);
            foreach (var publishInfo in publishInfos)
            {
                if (publishInfo.PublicationTarget == target)
                {
                    return publishInfo.PublishedAt;
                }
            }
            return DateTime.MinValue;
        }

        private string GetNavigationTitle(Page page)
        {
            Component mainComp = GetMainComponentOnPage(page);
            var title =  GetNavigationTitleFromComp(mainComp);
            return String.IsNullOrEmpty(title) ? StripPrefix(page.Title) : title;
        }

        private static Component GetMainComponentOnPage(Page page)
        {
            foreach (var cp in page.ComponentPresentations)
            {
                var region = GetRegionFromComponentPresentation(cp);
                if (region == MainRegionName)
                {
                    return cp.Component;
                }
            }
            return null;
        }

        private static string GetRegionFromComponentPresentation(ComponentPresentation cp)
        {
            var match = Regex.Match(cp.ComponentTemplate.Title, @".*?\[(.*?)\]");
            if (match.Success)
            {
                return match.Groups[1].Value;
            }
            //default region name
            return MainRegionName;
        }

        private static string GetNavigationTitleFromComp(Component mainComp)
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

        private static string GetUrl(Page page)
        {
            String url = page.PublishLocationUrl;
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
            return false;
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

    #region Sitemap Classes


    public class SitemapItem
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
        public DateTime PublishedDate { get; set; }
        public bool Visible { get; set; }
    }


    #endregion Sitemap Classes
}


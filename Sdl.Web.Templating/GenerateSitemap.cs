using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Script.Serialization;
using Sdl.Web.Templating.ExtensionMethods;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Templating.Assembly;
using Tridion.ContentManager;
using System.Xml;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.Publishing;
using System.Text.RegularExpressions;
using System.Xml.Linq;

namespace Sdl.Web.Templating
{
    /// <summary>
    /// Generates sitemap JSON. Should be used in a page template
    /// </summary>
    [TcmTemplateTitle("Generate Sitemap")]
    public class GenerateSiteMap : Sdl.Web.Templating.TemplateBase.TemplateBase
    {
        private StructureGroup _startPoint;

        public override void Transform(Engine engine, Package package)
        {
            this.Initialize(engine, package);
            if (this.GetPage() != null)
            {
                _startPoint = this.GetPublication().RootStructureGroup;

                if (_startPoint != null)
                {
                    string nav = GenerateNavigation();
                    if (!string.IsNullOrEmpty(GenerateNavigation()))
                    {
                        package.PushItem(Package.OutputName, package.CreateStringItem(ContentType.Text, nav));
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

        private SitemapFolder GenerateStructureGroupNavigation(StructureGroup startPoint)
        {
            var root = GenerateFolderNode(startPoint);
            var orderedDocument = GetItemsInFolderAsAList(startPoint);

            foreach (XElement pgNode in orderedDocument)
            {
                Page page = MEngine.GetObject(pgNode.Attribute("ID").Value) as Page;
                if (page != null)
                {
                    if (IsPublished(page) && isVisible(pgNode.Attribute("Title").Value))
                        root.Items.Add(GeneratePageNode(page));
                }
                else
                {
                    if (isVisible(pgNode.Attribute("Title").Value))
                        root.Items.Add(GenerateStructureGroupNavigation(MEngine.GetObject(pgNode.Attribute("ID").Value) as StructureGroup));
                }

            }
            return root;
        }

        private SitemapFolder GenerateFolderNode(StructureGroup startPoint)
        {
            SitemapFolder root = new SitemapFolder(GetNavigationTitle(startPoint));

            root.Id = startPoint.Id;
            root.Url = GetUrl(startPoint);
            root.Type = ItemType.StructureGroup.ToString();

            return root;
        }

        private string GetNavigationTitle(StructureGroup sg)
        {
            string result = Regex.Replace(sg.Title, @"^\d{3}\s", string.Empty);
            return result;
        }

        private string GetUrl(StructureGroup sg)
        {
            String url = sg.PublishLocationUrl;
            //TODO: Logic can be included here to be able to add external urls
            return System.Web.HttpUtility.UrlDecode(url);
        }

        private SitemapPage GeneratePageNode(Page page)
        {
            SitemapPage sitemapPage = new SitemapPage(GetNavigationTitle(page));

            sitemapPage.Id = page.Id;
            sitemapPage.Url = GetUrl(page);
            sitemapPage.Type = ItemType.Page.ToString();

            return sitemapPage;
        }

        private string GetNavigationTitle(Page page)
        {
            foreach (var cp in page.ComponentPresentations)
            {
                ItemFields meta = null;
                if (cp.Component.Metadata != null)
                {
                    //TODO: Implement this on the schemas
                    ItemFields fields = new ItemFields(cp.Component.Metadata, cp.Component.MetadataSchema);
                    meta = fields.GetEmbeddedField("StandardMetaData");
                    if (meta != null)
                    {
                        string title = meta.GetTextValue("NavigationTitle");
                        if (!string.IsNullOrEmpty(title)) return title;
                        break;
                    }
                }
            }
            string result = Regex.Replace(page.Title, @"^\d{3}\s", string.Empty);
            return result;
        }

        private string GetUrl(Page page)
        {
            String url = page.PublishLocationUrl;
            return System.Web.HttpUtility.UrlDecode(url);
        }

        private List<XElement> GetItemsInFolderAsAList(StructureGroup startPoint)
        {
            var filter = new OrganizationalItemItemsFilter(MEngine.GetSession());
            //get pages first to see if they have to appear in nav 
            filter.ItemTypes = new List<ItemType> { ItemType.Page, ItemType.StructureGroup };
            filter.BaseColumns = ListBaseColumns.Extended;
            XmlElement pagesXml = startPoint.GetListItems(filter);
            XmlDocument rootDocument = new XmlDocument();
            XDocument pageDoc = new XDocument();

            rootDocument.LoadXml(pagesXml.OuterXml);
            pageDoc = XDocument.Parse(rootDocument.OuterXml);

            var orderedDocument = (from XElement el in pageDoc.Root.Descendants()
                                   orderby el.Attribute("Title").Value
                                   select el).ToList();
            return orderedDocument;
        }

        private bool IsPublished(Page page)
        {
            if (MEngine.PublishingContext.PublicationTarget != null)
                return PublishEngine.IsPublished(page, MEngine.PublishingContext.PublicationTarget);
            return false;
        }

        private bool isVisible(string title)
        {
            // return !page.PublishLocationUrl.ToLower().EndsWith("index.aspx");
            Match match = Regex.Match(title, @"^\d{3}\s");
            return match.Success;
        }
    }

    #region Sitemap Classes

    public abstract class SitemapItem
    {
        public string Title { get; set; }
        public string Url { get; set; }
        public string Id { get; set; }
    }

    public class SitemapFolder : SitemapItem
    {
        public SitemapFolder(String title)
        {
            Title = title;
            Items = new List<SitemapItem>();
        }

        public List<SitemapItem> Items { get; set; }
        public string Type { get; set; }
    }

    public class SitemapPage : SitemapItem
    {
        public SitemapPage(String title)
        {
            Title = title;
        }

        public string Type { get; set; }
    }

    #endregion Sitemap Classes





}


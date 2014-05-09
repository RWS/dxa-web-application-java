using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Web.Script.Serialization;
using System.Xml;
using System.Xml.Linq;
using Sdl.Web.Templating.ExtensionMethods;
using Tridion.ContentManager;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Publishing;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;

namespace Sdl.Web.Templating
{
    /// <summary>
    /// Generates sitemap JSON. Should be used in a page template
    /// </summary>
    [TcmTemplateTitle("Generate Sitemap")]
    public class GenerateSiteMap : TemplateBase.TemplateBase
    {
        private StructureGroup _startPoint;

        public override void Transform(Engine engine, Package package)
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

        private SitemapItem GenerateStructureGroupNavigation(StructureGroup startPoint)
        {
            var root = GenerateFolderNode(startPoint);
            var orderedDocument = GetItemsInFolderAsAList(startPoint);

            foreach (XElement pgNode in orderedDocument)
            {
                Page page = MEngine.GetObject(pgNode.Attribute("ID").Value) as Page;
                if (page != null)
                {
                    if (IsPublished(page) && IsVisible(pgNode.Attribute("Title").Value))
                    {
                        root.Items.Add(GeneratePageNode(page));
                    }
                }
                else
                {
                    if (IsVisible(pgNode.Attribute("Title").Value))
                    {
                        root.Items.Add(GenerateStructureGroupNavigation(MEngine.GetObject(pgNode.Attribute("ID").Value) as StructureGroup));
                    }
                }

            }
            return root;
        }

        private static SitemapItem GenerateFolderNode(StructureGroup startPoint)
        {
            SitemapItem root = new SitemapItem(GetNavigationTitle(startPoint))
                {
                    Id = startPoint.Id,
                    Url = GetUrl(startPoint),
                    Type = ItemType.StructureGroup.ToString()
                };

            return root;
        }

        private static string GetNavigationTitle(StructureGroup sg)
        {
            string result = Regex.Replace(sg.Title, @"^\d{3}\s", string.Empty);
            return result;
        }

        private static string GetUrl(StructureGroup sg)
        {
            String url = sg.PublishLocationUrl;
            //TODO: Logic can be included here to be able to add external urls
            return System.Web.HttpUtility.UrlDecode(url);
        }

        private SitemapItem GeneratePageNode(Page page)
        {
            SitemapItem SitemapItem = new SitemapItem(GetNavigationTitle(page))
                {
                    Id = page.Id,
                    Url = GetUrl(page),
                    Type = ItemType.Page.ToString(),
                    PublishedDate = GetPublishedDate(page, MEngine.PublishingContext.PublicationTarget)
                };

            return SitemapItem;
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

        private static string GetNavigationTitle(Page page)
        {
            foreach (var cp in page.ComponentPresentations)
            {
                if (cp.Component.Metadata != null)
                {
                    //TODO: Implement this on the schemas
                    ItemFields fields = new ItemFields(cp.Component.Metadata, cp.Component.MetadataSchema);
                    ItemFields meta = fields.GetEmbeddedField("StandardMetaData");
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

        private static string GetUrl(Page page)
        {
            String url = page.PublishLocationUrl;
            return System.Web.HttpUtility.UrlDecode(url);
        }

        private IEnumerable<XElement> GetItemsInFolderAsAList(StructureGroup startPoint)
        {
            var filter = new OrganizationalItemItemsFilter(MEngine.GetSession())
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
            if (MEngine.PublishingContext.PublicationTarget != null)
            {
                return PublishEngine.IsPublished(page, MEngine.PublishingContext.PublicationTarget);
            }
            return false;
        }

        private static bool IsVisible(string title)
        {
            // return !page.PublishLocationUrl.ToLower().EndsWith("index.aspx");
            Match match = Regex.Match(title, @"^\d{3}\s");
            return match.Success;
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
            return value.Replace(".html","");
        }

        public string Id { get; set; }
        public string Type { get; set; }
        public List<SitemapItem> Items { get; set; }
        public DateTime PublishedDate { get; set; }
    }


    #endregion Sitemap Classes
}


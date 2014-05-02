using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web;
using System.Web.Script.Serialization;
using Sdl.Web.ContentManagement.ExtensionMethods;
using Sdl.Web.ContentManagement.Templating;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Templating.Assembly;
using Tridion.ContentManager;
using System.Xml;
using Tridion.ContentManager.ContentManagement;
using System.IO;
using Tridion.ContentManager.Publishing;
using System.Text.RegularExpressions;
using System.Xml.Linq;

namespace Sdl.Web.Templating

{
    /// <summary>
    /// Generates sitemap JSON. Should be used in a page template
    /// </summary>
    [TcmTemplateTitle("Generate Sitemap")]
    public class GenerateSiteMap : TemplateBase
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
                        package.PushItem(Package.OutputName, package.CreateStringItem(ContentType.Xml, nav));
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
            return String.Empty;
        }

        private SitemapFolder GenerateStructureGroupNavigation(StructureGroup startPoint)
        {
            var root = GenerateFolderNode(startPoint);
         
            try
            {
                var orderedDocument = GetItemsInFolderAsAList(startPoint);
                foreach (XElement pgNode in orderedDocument)
                {
                    Page page = m_Engine.GetObject(pgNode.Attribute("ID").Value) as Page;
                    if (page != null)                                          
                        if (IsPublished(page) && IsNavigationPage(pgNode.Attribute("Title").Value))                                               
                                root.Items.Add(GeneratePageNode(page, pgNode.Attribute("Title").Value));                                          
                    else                    
                        if (IsNavigationStructureGroup(pgNode.Attribute("Title").Value))                                                   
                            GenerateStructureGroupNavigation(m_Engine.GetObject(pgNode.Attribute("ID").Value) as StructureGroup);                                            
                }
            }
            catch (Exception ex)
            {
                Logger.Error("An error occured building the Navigation: " + ex.ToString());
            }

            return root;
        }

        private List<XElement> GetItemsInFolderAsAList(StructureGroup startPoint)
        {
            var filter = new OrganizationalItemItemsFilter(m_Engine.GetSession());
            //get pages first to see if they have to appear in nav 
            filter.ItemTypes = new List<ItemType> {ItemType.Page, ItemType.StructureGroup};
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
            bool isPublished = false;
            if (m_Engine.PublishingContext.PublicationTarget != null)
                isPublished = PublishEngine.IsPublished(page, m_Engine.PublishingContext.PublicationTarget);
            return isPublished;
        }

        private SitemapFolder GenerateFolderNode(StructureGroup startPoint)
        {
            SitemapFolder root = new SitemapFolder(GetNavigationTitle(startPoint));

            root.Id = root.Title;
            //TODO: Add  .WriteAttributeString("fullTitle", startPoint.Title);
            root.Url = GetUrl(startPoint);
            //TODO: Analyze writer.WriteAttributeString("type", ItemType.StructureGroup.ToString());
            return root;
        }
        private SitemapPage GeneratePageNode(Page page, string pTitle)
        {

            bool isPublished = true;
            if (m_Engine.PublishingContext.PublicationTarget != null)
                isPublished = PublishEngine.IsPublished(page, m_Engine.PublishingContext.PublicationTarget);
            if (isPublished)
            {
                SitemapPage sitemapPage = new SitemapPage(GetNavigationTitle(page));
                return sitemapPage;

            }
            return null;
        }

        private string GetUrl(StructureGroup sg)
        {
            String url = sg.PublishLocationUrl;
            
            //TODO: Logic can be included here to be able to add external urls
   
            //ASP.NET sitemap provider requires unencoded urls
            return System.Web.HttpUtility.UrlDecode(url);
          
        }
        // Check if a page is visible
        private bool CheckVisible(Page page)
        {
            // return !page.PublishLocationUrl.ToLower().EndsWith("index.aspx");
            Match match = Regex.Match(page.Title, @"^\d{3}\s");
            return match.Success;
        }

        //private string GetUrl(Page page)
        //{
        //    string result = string.Empty;
        //    if (page.PublishLocationUrl.EndsWith(".aspx"))
        //    {
        //        result = page.PublishLocationUrl.Substring(0, page.PublishLocationUrl.LastIndexOf("."));
        //    }
        //    else
        //    {
        //        result = page.PublishLocationUrl;
        //    }
        //    //ASP.NET sitemap provider requires unencoded urls
        //    return System.Web.HttpUtility.UrlDecode(result);
        //}

        private string GetNavigationTitle(StructureGroup _startPoint)
        {
            string result = Regex.Replace(_startPoint.Title, @"^\d{3}\s", string.Empty);
            return result;
        }

        private bool IsNavigationStructureGroup(string sgTitle)
        {
            //Match match = Regex.Match(sgTitle, @"^\d{3}\s");
            //return match.Success;
            return true;
        }

        private string GetNavigationTitle(Page page)
        {
            foreach (var cp in page.ComponentPresentations)
            {
                ItemFields meta = null;
                if (cp.Component.Metadata != null)
                {
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

        private bool IsNavigationPage(string pTitle)
        {
            return true;
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

    }

    public class SitemapPage : SitemapItem
    {
        public SitemapPage(String title)
        {
            Title = title;
        }

    }

#endregion Sitemap Classes





}


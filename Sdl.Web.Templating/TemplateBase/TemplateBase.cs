using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Tridion.ContentManager.Templating.Assembly;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.Publishing.Rendering;
using Tridion.ContentManager.Publishing;
using Tridion.ContentManager;
using System.IO;
using System.Xml;
using Tridion.ContentManager.ContentManagement.Fields;
using System.Globalization;

namespace Sdl.Web.ContentManagement.Templating
{
    /// <summary>
    /// Base class for common functionality used by TBBs
    /// </summary>
    public abstract class TemplateBase : ITemplate
    {
        protected Engine m_Engine;
        protected Package m_Package;
        private TemplatingLogger m_Logger;
        protected int m_RenderContext = -1;

        protected TemplatingLogger Logger
        {
            get
            {
                if (m_Logger == null) m_Logger = TemplatingLogger.GetLogger(this.GetType());

                return m_Logger;
            }
        }
        /// <summary>
        /// Initializes the engine and package to use in this TemplateBase object.
        /// </summary>
        /// <param name="engine">The engine to use in calls to the other methods of this TemplateBase object</param>
        /// <param name="package">The package to use in calls to the other methods of this TemplateBase object</param>
        protected void Initialize(Engine engine, Package package)
        {
            m_Engine = engine;
            m_Package = package;
        }

        public virtual void Transform(Engine engine, Package package) { }

        /// <summary>
        /// Checks whether the TemplateBase object has been initialized correctly.
        /// This method should be called from any method that requires the <c>m_Engine</c>, 
        /// <c>m_Package</c> or <c>_log</c> member fields.
        /// </summary>
        protected void CheckInitialized()
        {
            if (m_Engine == null || m_Package == null)
            {
                throw new InvalidOperationException("This method can not be invoked, unless Initialize has been called");
            }
        }

        #region Get context objects and information 
 
        /// <summary>
        /// True if the rendering context is a page, rather than component
        /// </summary>
        public bool IsPageTemplate()
        {
          
                if (m_RenderContext == -1)
                {
                    if (m_Engine.PublishingContext.ResolvedItem.Item is Page)
                        m_RenderContext = 1;
                    else
                        m_RenderContext = 0;
                }
                if (m_RenderContext == 1)
                    return true;
                else
                    return false;
            
        }
        /// <summary>
        /// Returns the component object that is defined in the package for this template.
        /// </summary>
        /// <remarks>
        /// This method should only be called when there is an actual Component item in the package. 
        /// It does not currently handle the situation where no such item is available.
        /// </remarks>
        /// <returns>the component object that is defined in the package for this template.</returns>
        public Component GetComponent()
        {
            CheckInitialized();
            Item component = m_Package.GetByName("Component");
            if (component != null)
            {
                return (Component)m_Engine.GetObject(component.GetAsSource().GetValue("ID"));
            }
            else
            {
                return null;
            }
        }
        
        /// <summary>
        /// Returns the Template from the resolved item if it's a Component Template
        /// </summary>
        /// <returns>A Component Template or null</returns>
        protected ComponentTemplate GetComponentTemplate()
        {
            CheckInitialized();
            Template template = m_Engine.PublishingContext.ResolvedItem.Template;

            // "if (template is ComponentTemplate)" might work instead
            if (template.GetType().Name.Equals("ComponentTemplate"))
            {
                return (ComponentTemplate)template;
            }
            else
            {
                return null;
            }
        }

        /// <summary>
        /// Returns the page object that is defined in the package for this template.
        /// </summary>
        /// <remarks>
        /// This method should only be called when there is an actual Page item in the package. 
        /// It does not currently handle the situation where no such item is available.
        /// </remarks>
        /// <returns>the page object that is defined in the package for this template.</returns>
        public Page GetPage()
        {
            CheckInitialized();
            //first try to get from the render context
            RenderContext renderContext = m_Engine.PublishingContext.RenderContext;
            if (renderContext != null)
            {
                Page contextPage = renderContext.ContextItem as Page;
                if (contextPage != null)
                    return contextPage;
            }
            Item pageItem = m_Package.GetByType(ContentType.Page);
            if (pageItem != null)
                return (Page)m_Engine.GetObject(pageItem.GetAsSource().GetValue("ID"));

            return null;
        }

        /// <summary>
        /// Returns the publication object that can be determined from the package for this template.
        /// </summary>
        /// <remarks>
        /// This method currently depends on a Page item being available in the package, meaning that
        /// it will only work when invoked from a Page Template.
        /// 
        /// </remarks>
        /// <returns>the Publication object that can be determined from the package for this template.</returns>
        protected Publication GetPublication()
        {
            CheckInitialized();

            RepositoryLocalObject pubItem = null;
            Repository repository = null;

            if (m_Package.GetByType(ContentType.Page) != null)
                pubItem = GetPage();
            else
                pubItem = GetComponent();

            if (pubItem != null) repository = pubItem.ContextRepository;

            return repository as Publication;
        }

        protected bool IsPublishingToStaging()
        {
            if (m_Engine.PublishingContext != null && m_Engine.PublishingContext.PublicationTarget != null)
            {
                return m_Engine.PublishingContext.PublicationTarget != null ? m_Engine.PublishingContext.PublicationTarget.Title.ToLower().Contains("staging") : false;
            }
            return false;
        }

        protected bool IsPreviewMode()
        {
            return m_Engine.RenderMode == RenderMode.PreviewDynamic || m_Engine.RenderMode == RenderMode.PreviewStatic;
        }

        #endregion

        #region Useful Bits and Pieces

        /// <summary>
        /// Put the context component back on top of the package stack
        /// As some TBBs (like SiteEdit ones) rely on this being the first
        /// Component in the stack
        /// </summary>
        protected void PutContextComponentOnTop()
        {
            Item mainComponent = m_Package.GetByName("Component");
            if (mainComponent != null)
            {
                m_Package.Remove(mainComponent);
                m_Package.PushItem("Component", mainComponent);
            }
        }

        protected TcmUri GetDefaultTemplate(Schema schema)
        {
            var templates = this.GetUsingItems(schema, ItemType.ComponentTemplate);
            if (templates == null || templates.Count == 0)
            {
                return null;
            }
            else
            {
                var templateUri = templates.Where(t => t.Value.Contains("[Default]")).Select(t => t.Key).FirstOrDefault();
                if (templateUri == null)
                {
                    templateUri = templates[0].Key;
                }
                return templateUri;
            }
        }

        #endregion

        #region Caching Functions

        /// <summary>
        /// Push a dictionary of package items into the package, and  store them in the 
        /// Publishing Context Variables, so they can be reused across multiple template
        /// executions in a publish transaction.
        /// This acts as a caching mechanism, so that global items like config components
        /// need only be loaded once within a publish transaction. Items can be retrieved 
        /// from the context variable and pushed into the package using the PushFromContextVariable
        /// method
        /// CAUTION: Only global package items should be pushed, not items that will differ
        /// from component presentation to component presentation, or page to page
        /// </summary>
        /// <param name="items">The Dictionary of package items to push (the key for each dictionary item will be used as the name of the item in the package)</param>
        /// <param name="variableName">The context variable name to store these items in</param>
        protected void PushAndAddToContextVariables(Dictionary<string, Item> items, string variableName)
        {
            if (items != null)
            {
                foreach (string key in items.Keys)
                {
                    m_Package.PushItem(key, items[key]);
                }
            }
            if (m_Engine.PublishingContext.RenderContext != null && !m_Engine.PublishingContext.RenderContext.ContextVariables.Contains(variableName))
            {
                m_Engine.PublishingContext.RenderContext.ContextVariables.Add(variableName, items);
            }
        }
        
        /// <summary>
        /// Load package items from the publishing context variables and push them into the package.
        /// The context variables are used as a cache, so they can be populated (using PushAndAddToContextVariables)
        /// in the first template execution in a publish transaction, and reused in all others
        /// </summary>
        /// <param name="variableName">The context variable name which contains the items to push</param>
        /// <returns>false if the context variable is empty, true otherwise</returns>
        protected bool PushFromContextVariable(string variableName)
        {

            
            
            if (m_Engine.PublishingContext.RenderContext != null && m_Engine.PublishingContext.RenderContext.ContextVariables.Contains(variableName))
            {
                
                Dictionary<string, Item> items = m_Engine.PublishingContext.RenderContext.ContextVariables[variableName] as Dictionary<string, Item>;
                if (items != null)
                {
                    foreach (string key in items.Keys)
                    {
                        m_Package.PushItem(key, items[key]);
                    }
                }
                return true;
            }
            return false;
        }

        #endregion

        #region Handling Binaries

        /// <summary>
        /// Add binaries from a given folder (and subfolders recursively) to the package
        /// publish them and update the package item with the publish path
        /// </summary>
        /// <param name="rootSGWebDavUrl">Root Structure Group to add in</param>
        /// <param name="folder">Root folder to look in</param>
        /// <param name="path">path from root structure group to add in</param>
        protected void AddBinariesFromFolder(string rootSGWebDavUrl, Folder folder, string path)
        {
            StructureGroup sg = null;
            //Loop through all components in the folder
            foreach (KeyValuePair<TcmUri, string> item in GetOrganizationalItemContents(folder, ItemType.Component, false))
            {
                Component mmComp;
                mmComp = m_Engine.GetObject(item.Key) as Component;
                //if its a MM comp then add it to the package, and publish it
                if (mmComp.ComponentType == ComponentType.Multimedia)
                {
                    if (sg == null)
                    {
                        //Try to get a reference to the sub Structure Group based on the folder title
                        sg = m_Engine.GetObject(rootSGWebDavUrl + path) as StructureGroup;
                        if (sg == null)
                        {
                            Exception ex = new Exception(String.Format("Could not find Structure Group {0}. Please create this and republish", rootSGWebDavUrl + path));
                            Logger.Error(ex.Message);
                            throw ex;
                        }
                    }
                    AddBinary(mmComp, sg);
                }

            }
            //Loop through all subfolders to recurse
            foreach (KeyValuePair<TcmUri, string> item in GetOrganizationalItemContents(folder, ItemType.Folder, false))
            {
                Logger.Info(String.Format("Processing subfolder: {0} ({1})", item.Value, item.Key));
                Folder subFolder = m_Engine.GetObject(item.Key) as Folder;
                AddBinariesFromFolder(rootSGWebDavUrl, subFolder, path + "/" + subFolder.Title);
            }
        }

        /// <summary>
        /// Add a binary to the package and ensure it is published into the given structure group
        /// </summary>
        /// <param name="mmComp">The binary to add</param>
        /// <param name="sg">The target SG</param>
        protected void AddBinary(Component mmComp, StructureGroup sg)
        {
            Item packageItem = null;
            string filename = GetFilename(mmComp.BinaryContent.Filename);

            //Check if the binary is already in the package (for example, if the DWT already added it
            packageItem = m_Package.GetByName(filename);
            if (packageItem != null)
            {
                Logger.Debug("An item with the same name exists in the package");
                KeyValuePair<string, string> pair = new KeyValuePair<string, string>("TCMURI", mmComp.Id.ToString());
                if (!packageItem.Properties.Contains(pair))
                {
                    //its a different item so we should push our item in the package
                    packageItem = null;
                }
            }

            //if its not in the package, add it
            if (packageItem == null)
            {
                Logger.Debug(String.Format("Pushing item {0} to the package", filename));
                packageItem = m_Package.CreateMultimediaItem(mmComp.Id);
                m_Package.PushItem(filename, packageItem);
            }

            //Publish the binary into the appropriate SG
            using (Stream itemStream = packageItem.GetAsStream())
            {
                try
                {
                    byte[] data = new byte[itemStream.Length];
                    itemStream.Read(data, 0, data.Length);
                    Logger.Info(String.Format("Adding binary component {0}({1}) ", mmComp.Title, mmComp.Id.ToString()));
                    string publishedPath = m_Engine.AddBinary(mmComp.Id, null, sg.Id, data, filename);
                    packageItem.Properties[Item.ItemPropertyPublishedPath] = publishedPath;
                }
                finally
                {
                    itemStream.Close();
                }
            }
        }

        /// <summary>
        /// Publish a binary with the tcm uri as part of the file name to ensure uniqueness
        /// </summary>
        /// <param name="comp">The binary to publish</param>
        /// <returns>The publish path of the binary</returns>
        public string AddBinaryWithUniqueFilename(Component comp, StructureGroup sg = null)
        {
            MemoryStream ms = new MemoryStream();
            comp.BinaryContent.WriteToStream(ms);
            string filename = GetFilename(comp.BinaryContent.Filename);
            //make sure filename is unique by appending pub and component id
            string suffix = GetBinaryFileSuffix(comp.Id.ToString());
            int pos = filename.LastIndexOf(".");
            if (pos > 0)
            {
                filename = filename.Substring(0, pos) + suffix + filename.Substring(pos);
            }
            else
                filename += suffix;

            Logger.Debug(String.Format("Adding binary to package: {0}", filename));
            return m_Engine.AddBinary(comp.Id, TcmUri.UriNull, sg!=null ? sg.Id : null, ms.ToArray(), filename);

        }

        protected string GetBinaryFileSuffix(string compUri)
        {
            TcmUri uri = new TcmUri(compUri);
            return "-" + uri.ItemId;
        }

        protected string GetFilename(string fullpath)
        {
            if (fullpath.Contains(@"\"))
            {
                int pos = fullpath.LastIndexOf(@"\");
                return fullpath.Substring(pos + 1);
            }
            return fullpath;
        }

        #endregion

        #region TOM.NET Helper functions
        protected List<KeyValuePair<TcmUri, string>> GetOrganizationalItemContents(OrganizationalItem orgItem, ItemType itemType, bool recursive)
        {
            var filter = new OrganizationalItemItemsFilter(orgItem.Session);
            filter.ItemTypes = new List<ItemType> { itemType};
            filter.Recursive = recursive;
            return XmlElementToTcmUriList(orgItem.GetListItems(filter));
        }

        protected List<KeyValuePair<TcmUri, string>> GetUsingItems(RepositoryLocalObject subject, ItemType itemType)
        {
            UsingItemsFilter filter = new UsingItemsFilter(m_Engine.GetSession());
            filter.ItemTypes = new List<ItemType> { itemType };
            filter.BaseColumns = ListBaseColumns.IdAndTitle;
            return XmlElementToTcmUriList(subject.GetListUsingItems(filter));
        }

        protected List<KeyValuePair<TcmUri, string>> XmlElementToTcmUriList(XmlElement data)
        {
            List<KeyValuePair<TcmUri, string>> res = new List<KeyValuePair<TcmUri, string>>();
            foreach (XmlNode item in data.SelectNodes("/*/*"))
            {
                string title = item.Attributes["Title"].Value;
                TcmUri id = new TcmUri(item.Attributes["ID"].Value);
                res.Add(new KeyValuePair<TcmUri, string>(id, title));
            }
            return res;
        }
        #endregion

        #region Page and Structure Group Title processing

        protected string GetPageOrStructureGroupTitle(RepositoryLocalObject pageOrSg)
        {
            if (pageOrSg is Page)
                return GetPageTitle(pageOrSg as Page);
            else
                return GetStructureGroupTitle(pageOrSg as StructureGroup);
        }
        
        protected string GetPageTitle(Page page)
        {
            string title = null;

            //1. Try to take the value from content or metadata of the first component in the page
            if (page.ComponentPresentations.Count > 0)
            {
                //Add your own logic here to read the title from the content/metadata
                Component firstComp = page.ComponentPresentations[0].Component;
                if (firstComp != null)
                {
                    string metaFieldName = "ShortTitle";
                    string fieldName = "Heading";
                    XmlElement data = firstComp.Metadata;
                    XmlNode titleField = null;
                    if (data!=null)
                        titleField = data.SelectSingleNode(String.Format("//*[local-name()='{0}']", metaFieldName));
                    if (titleField != null)
                        title = titleField.InnerText; 
                    data = firstComp.Content;
                    titleField = data.SelectSingleNode(String.Format("//*[local-name()='{0}']", fieldName));
                    if (titleField != null)
                        title = titleField.InnerText;
                }
            }
            //2. Fallback on page title
            if (String.IsNullOrEmpty(title))
            {
                title = StripPrefix(page.Title);
            }
            return title;
        }

        protected string GetStructureGroupTitle(StructureGroup sg)
        {
            return this.StripPrefix(sg.Title);
        }

        /// <summary>
        /// Removes the prefix of a string based on the following convention:
        /// 1) Number prefix - '100 The Title' becomes 'The Title'
        /// It either contains three digits and be followed by space
        /// </summary>
        /// <param name="title">The string to strip</param>
        /// <returns>The stripped string</returns>
        public string StripPrefix(string title)
        {
            int length = 4;
            if (title.Length < 4)
                length = title.Length - 1;
            string prefix = title.Substring(0, length);
            int result = 0;
            //Handle number prefix
            if (int.TryParse(prefix, out result))
            {
                title = title.Substring(title.IndexOf(" ") + 1);
            }
            title = title.Trim();
            return title;
        }
        #endregion

        #region Processing TBB Parameters

        protected string GetFolderWebdavUrlFromPackageVariable(string packageVariable)
        {
            var folder = m_Package.GetValue(packageVariable);
            if (!String.IsNullOrEmpty(folder))
            {
                folder = this.GetPublication().RootFolder.WebDavUrl + "/" + folder;
                if (m_Engine.GetSession().IsExistingObject(folder))
                {
                    return folder;
                }
                else
                {
                    throw new Exception(String.Format("Folder {0} does not exist. Please check TBB parameters/package variables", folder));
                }
            }
            else
            {
                throw new Exception(String.Format("No {0} parameter/package variable found. Please check TBB parameters/package variables", packageVariable));
            }
        }

        #endregion

        #region Config and resources

        protected string Config(string key, string defaultValue)
        {
            return m_Package.GetValue(key) ?? defaultValue;
        }

        protected string Config(string key)
        {
            return m_Package.GetValue(key) ?? key;
        }

        protected string GetEnvironmentIdentifier()
        {
            return Config("GlobalConfig.EnvironmentIdentifier");
        }

        protected string GetStaticsBaseUrlAndPath()
        {
            string host = string.Empty;
            string path = string.Empty;
            if (this.IsPreviewMode() || this.IsPublishingToStaging())
            {
                host = Config("EnvironmentConfig.StaticsHostStaging");
                path = Config("EnvironmentConfig.StaticsRootStaging");
            }
            else
            {
                host = Config("EnvironmentConfig.StaticsHostLive");
                path = Config("EnvironmentConfig.StaticsRootLive");
            }
            if (!string.IsNullOrEmpty(host))
            {
                return host + path;
            }
            return path;
        }

        protected string FormatDate(DateTime date, string format = null)
        {
            var dateFormat = format ?? Config("SiteConfig.FormatDateTime",null);
            var culture = Config("SiteConfig.Culture", null);
            return culture==null ?  date.ToString(dateFormat) : date.ToString(dateFormat, new CultureInfo(culture));
        }

        #endregion
    }
}

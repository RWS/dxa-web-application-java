using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Web.Script.Serialization;
using System.Xml;
using System.Xml.Linq;
using Tridion.ContentManager;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Publishing;
using Tridion.ContentManager.Publishing.Rendering;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;

namespace Sdl.Web.Tridion.Common
{
    /// <summary>
    /// Base class for common functionality used by TBBs
    /// </summary>
    public abstract class TemplateBase : ITemplate
    {
        protected Engine Engine;
        protected Package Package;
        protected int RenderContext = -1;

        protected const string JsonMimetype = "application/json";
        protected const string JsonExtension = ".json";
        protected const string BootstrapFilename = "_all";
        
        protected TemplatingLogger Logger
        {
            get
            {
                if (_mLogger == null)
                {
                    _mLogger = TemplatingLogger.GetLogger(GetType());
                }

                return _mLogger;
            }
        }

        private TemplatingLogger _mLogger;

        /// <summary>
        /// Initializes the engine and package to use in this TemplateBase object.
        /// </summary>
        /// <param name="engine">The engine to use in calls to the other methods of this TemplateBase object</param>
        /// <param name="package">The package to use in calls to the other methods of this TemplateBase object</param>
        protected void Initialize(Engine engine, Package package)
        {
            Engine = engine;
            Package = package;
        }

        public virtual void Transform(Engine engine, Package package) { }

        /// <summary>
        /// Checks whether the TemplateBase object has been initialized correctly.
        /// This method should be called from any method that requires the <c>m_Engine</c>, 
        /// <c>m_Package</c> or <c>_log</c> member fields.
        /// </summary>
        protected void CheckInitialized()
        {
            if (Engine == null || Package == null)
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

            if (RenderContext == -1)
            {
                if (Engine.PublishingContext.ResolvedItem.Item is Page)
                {
                    RenderContext = 1;
                }
                else
                {
                    RenderContext = 0;
                }
            }

            return RenderContext == 1;
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
            Item component = Package.GetByName(Package.ComponentName);
            if (component != null)
            {
                return (Component)Engine.GetObject(component.GetAsSource().GetValue("ID"));
            }

            return null;
        }

        /// <summary>
        /// Returns the Template from the resolved item if it's a Component Template
        /// </summary>
        /// <returns>A Component Template or null</returns>
        protected ComponentTemplate GetComponentTemplate()
        {
            CheckInitialized();
            Template template = Engine.PublishingContext.ResolvedItem.Template;

            // "if (template is ComponentTemplate)" might work instead
            if (template.GetType().Name.Equals(Package.ComponentTemplateName))
            {
                return (ComponentTemplate)template;
            }

            return null;
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
            RenderContext renderContext = Engine.PublishingContext.RenderContext;
            if (renderContext != null)
            {
                Page contextPage = renderContext.ContextItem as Page;
                if (contextPage != null)
                {
                    return contextPage;
                }
            }

            Item pageItem = Package.GetByType(ContentType.Page);
            if (pageItem != null)
            {
                return (Page)Engine.GetObject(pageItem.GetAsSource().GetValue("ID"));
            }

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

            RepositoryLocalObject pubItem;
            Repository repository = null;

            if (Package.GetByType(ContentType.Page) != null)
            {
                pubItem = GetPage();
            }
            else
            {
                pubItem = GetComponent();
            }

            if (pubItem != null)
            {
                repository = pubItem.ContextRepository;
            }

            return repository as Publication;
        }

        /// <summary>
        /// Determine if current action is publishing to a XPM enabled target.
        /// </summary>
        /// <returns>True if publishing to a target which is XPM enabled.</returns>
        protected bool IsPublishingToStaging()
        {
            return Utility.IsXpmEnabled(Engine.PublishingContext);
        }

        protected bool IsPreviewMode()
        {
            return Engine.RenderMode == RenderMode.PreviewDynamic || Engine.RenderMode == RenderMode.PreviewStatic;
        }

        protected bool IsMasterWebPublication(Publication publication)
        {
            if (publication.Metadata != null)
            {
                ItemFields meta = new ItemFields(publication.Metadata, publication.MetadataSchema);
                string isMaster = meta.GetTextValue("isMaster");
                if (!String.IsNullOrEmpty(isMaster))
                {
                    return true;
                }
            }
            return false;
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
            Item mainComponent = Package.GetByName("Component");
            if (mainComponent != null)
            {
                Package.Remove(mainComponent);
                Package.PushItem("Component", mainComponent);
            }
        }
        
        
        #endregion

        #region TOM.NET Helper functions
        protected List<KeyValuePair<TcmUri, string>> GetOrganizationalItemContents(OrganizationalItem orgItem, ItemType itemType, bool recursive)
        {
            OrganizationalItemItemsFilter filter = new OrganizationalItemItemsFilter(orgItem.Session)
                {
                    ItemTypes = new List<ItemType> { itemType },
                    Recursive = recursive
                };
            return XmlElementToTcmUriList(orgItem.GetListItems(filter));
        }

        protected OrganizationalItem GetChildOrganizationalItem(OrganizationalItem root, string title)
        {
            foreach (KeyValuePair<TcmUri, string> child in GetOrganizationalItemContents(root, root is Folder ? ItemType.Folder : ItemType.StructureGroup, false))
            {
                if (child.Value.ToLower() == title.ToLower())
                {
                    return (OrganizationalItem)Engine.GetObject(child.Key);
                }
            }
            return null;
        }

        protected List<KeyValuePair<TcmUri, string>> GetUsingItems(RepositoryLocalObject subject, ItemType itemType)
        {
            UsingItemsFilter filter = new UsingItemsFilter(Engine.GetSession())
                {
                    ItemTypes = new List<ItemType> { itemType },
                    BaseColumns = ListBaseColumns.IdAndTitle
                };
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

        #region Json Data Processing
        protected Dictionary<string, string> MergeData(Dictionary<string, string> source, Dictionary<string, string> mergeData)
        {
            foreach (string key in mergeData.Keys)
            {
                if (!source.ContainsKey(key))
                {
                    source.Add(key, mergeData[key]);
                }
                else
                {
                    Logger.Warning(String.Format("Duplicate key ('{0}') found when merging data. The second value will be skipped.", key));
                }
            }
            return source;
        }

        protected List<string> PublishJsonData(Dictionary<string, List<string>> settings, Component relatedComponent, string variantName, StructureGroup sg, bool isArray = false)
        {
            List<string> files = new List<string>();
            foreach (string key in settings.Keys)
            {
                files.Add(PublishJsonData(settings[key], relatedComponent, key, variantName+key, sg, isArray));
            }
            return files;
        }

        protected string PublishJsonData(Dictionary<string,string> data, Component relatedComponent, string filename, string variantName, StructureGroup sg, bool isArray = false)
        {
            return PublishJsonData(data.Select(i => String.Format("{0}:{1}", JsonEncode(i.Key), JsonEncode(i.Value))).ToList(), relatedComponent, filename, variantName, sg, isArray);
        }
            
        protected string PublishJsonData(List<string> settings, Component relatedComponent, string filename, string variantName, StructureGroup sg, bool isArray = false)
        {
            if (settings.Count > 0)
            {
                string json;
                if (isArray)
                {
                    json = String.Format("[{0}]", String.Join(",\n", settings));
                }
                else
                {
                    json = String.Format("{{{0}}}", String.Join(",\n", settings));
                }
                return PublishJson(json, relatedComponent, sg, filename, variantName);
            }
            return null;
        }

        protected string PublishBootstrapJson(List<string> filesCreated, Component relatedComponent, StructureGroup sg, string variantName = null, List<string> additionalData = null)
        {
            string extras = additionalData != null && additionalData.Count > 0 ? String.Join(",", additionalData) + "," : "";
            return PublishJson(String.Format("{{{0}\"files\":[{1}]}}", extras, String.Join(",", filesCreated.Where(i=>!String.IsNullOrEmpty(i)).ToList())), relatedComponent, sg, BootstrapFilename, variantName + "bootstrap");
        }

        protected string PublishJson(string json, Component relatedComponent, StructureGroup sg, string filename, string variantName)
        {
            Item jsonItem = Package.CreateStringItem(ContentType.Text, json);
            Binary binary = Engine.PublishingContext.RenderedItem.AddBinary(jsonItem.GetAsStream(), filename + JsonExtension, sg, variantName, relatedComponent, JsonMimetype);
            jsonItem.Properties[Item.ItemPropertyPublishedPath] = binary.Url;
            Package.PushItem(binary.Url, jsonItem);
            return JsonEncode(binary.Url);
        }

        protected Dictionary<string, string> ReadComponentData(Component comp)
        {
            Dictionary<string, string> settings = new Dictionary<string, string>();
            if (comp.Content!=null)
            {
                ItemFields fields = new ItemFields(comp.Content, comp.Schema);
                IEnumerable<ItemFields> configFields = fields.GetEmbeddedFields("settings");
                if (configFields.Any())
                {
                    //either schema is a generic multival embedded name/value
                    foreach (ItemFields setting in configFields)
                    {
                        string key = setting.GetTextValue("name");
                        if (!String.IsNullOrEmpty(key) && !settings.ContainsKey(key))
                        {
                            settings.Add(key, setting.GetTextValue("value"));
                        }
                        else
                        {
                            Logger.Warning(String.Format("Duplicate key found: '{0}' when processing component {1}", key, comp.Id));
                        }
                    }
                }
                else
                {
                    //... or its a custom schema with individual fields
                    foreach (ItemField field in fields)
                    {
                        //TODO - do we need to be smarter about date/number type fields?
                        string key = field.Name;
                        settings.Add(key, fields.GetSingleFieldValue(key));
                    }
                }
            }
            return settings;
        }

        protected string JsonEncode(object json)
        {
            JavaScriptSerializer serializer = new JavaScriptSerializer();
            return serializer.Serialize(json);
        }

        #endregion

        #region Module Data Processing

        protected StructureGroup GetSystemStructureGroup(string subStructureGroupTitle=null)
        {
            string webdavUrl = String.Format("{0}/_System{1}", GetPublication().RootStructureGroup.WebDavUrl, subStructureGroupTitle==null ? "" : "/" + subStructureGroupTitle);
            StructureGroup sg = Engine.GetObject(webdavUrl) as StructureGroup;
            if (sg == null)
            {
                throw new Exception(String.Format("Cannot find structure group with webdav URL: {0}", webdavUrl));
            }
            return sg;
        }

        protected Dictionary<string, Component> GetActiveModules(Component coreConfigComponent = null)
        {
            Schema moduleConfigSchema = coreConfigComponent != null ? coreConfigComponent.Schema : GetModuleConfigSchema();
            Dictionary<string, Component> results = new Dictionary<string, Component>();
            foreach (KeyValuePair<TcmUri, string> item in GetUsingItems(moduleConfigSchema, ItemType.Component))
            {
                try
                {
                    Component comp = (Component)Engine.GetObject(Engine.LocalizeUri(item.Key));
                    ItemFields fields = new ItemFields(comp.Content, comp.Schema);
                    string moduleName = GetModuleNameFromConfig(comp).ToLower();
                    if (fields.GetTextValue("isActive").ToLower() == "yes" && !results.ContainsKey(moduleName))
                    {
                        results.Add(moduleName, comp);
                    }
                }
                catch (Exception)
                {
                    //Do nothing, this module is not available in this publication
                }
            }
            return results;
        }

        private Schema GetModuleConfigSchema()
        {
            Publication pub = GetPublication();
            RepositoryItemsFilter filter = new RepositoryItemsFilter(pub.Session)
                {
                    ItemTypes = new List<ItemType> { ItemType.Schema },
                    Recursive = true
                };
            foreach (KeyValuePair<TcmUri, string> item in XmlElementToTcmUriList(GetPublication().GetListItems(filter)))
            {
                if (item.Value == "Module Configuration")
                {
                    return (Schema)pub.Session.GetObject(item.Key); 
                }
            }
            throw new Exception("Cannot find Schema named \"Module Configuration\"- please check that this has not been renamed.");
        }

        protected string GetModuleNameFromConfig(Component configComponent)
        {
            //Module config components are always found in /Modules/{Name}/System/, so the module name is defined to be the name of the folder 2 levels up.
            return configComponent.OrganizationalItem.OrganizationalItem.Title.ToLower();
        }

        protected string GetModulesRoot(Component configComponent)
        {
            //Module config components are always found in /Modules/{Name}/System/, so the module root is defined as the folder 3 levels up.
            return configComponent.OrganizationalItem.OrganizationalItem.OrganizationalItem.WebDavUrl;
        }

        protected string GetModuleNameFromItem(RepositoryLocalObject item, string moduleRoot)
        {
            //The module name is the name of the folder within the first level of the module root folder 
            //in which the item lives
            string fullItemWebdavUrl = item.WebDavUrl;
            if (fullItemWebdavUrl.StartsWith(moduleRoot))
            {
                Logger.Debug(fullItemWebdavUrl + ":" + moduleRoot);
                string res = fullItemWebdavUrl.Substring(moduleRoot.Length + 1);
                int pos = res.IndexOf("/", StringComparison.Ordinal);
                Logger.Debug(res);
                return res.Substring(0, pos).ToLower();
            }
            return null;
        }
        
        protected static string GetRegionName(ComponentTemplate template)
        {
            // check CT metadata
            if (template.MetadataSchema != null && template.Metadata != null)
            {
                ItemFields meta = new ItemFields(template.Metadata, template.MetadataSchema);

                string regionName = meta.GetTextValue("regionName");
                if (!String.IsNullOrEmpty(regionName))
                {
                    return regionName;
                }

                string regionViewName = meta.GetTextValue("regionView");
                if (!String.IsNullOrEmpty(regionViewName))
                {
                    // strip module from fully qualified name
                    // since we need just the region name here as the web application can't deal with fully qualified region names yet
                    return StripModuleFromName(regionViewName);
                }
            }

            // fallback use template title
            Match match = Regex.Match(template.Title, @".*?\[(.+?)\]");
            if (match.Success)
            {
                // strip module from fully qualified name
                // since we need just the region name here as the web application can't deal with fully qualified region names yet
                return StripModuleFromName(match.Groups[1].Value);
            }

            // default region name
            return "Main";
        }

        private static string StripModuleFromName(string name)
        {
            // split fully qualified view name on colon, use last part as unqualified view name
            string[] nameParts = name.Trim().Split(':');
            if (nameParts.Length > 1)
            {
                return nameParts[1];
            }

            return name;
        }
        #endregion

    }
}

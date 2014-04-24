using Sdl.Web.ContentManagement.Templating;
using System;
using System.Collections.Generic;
using System.Dynamic;
using System.IO;
using System.Linq;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Threading.Tasks;
using Tridion.ContentManager;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.Templating;
using Sdl.Web.ContentManagement.ExtensionMethods;
using Tridion.ContentManager.ContentManagement.Fields;
using System.Web.Helpers;
using Tridion.ContentManager.Templating.Assembly;
using System.Xml;
using System.Text.RegularExpressions;

namespace Sdl.Web.ContentManagement.Templating
{
    /// <summary>
    /// Publish all files required to make a version release for the implementation:
    /// * Configuration
    /// * Resources
    /// * Schema, Template and Taxonomy ids (these are published only from the default localization/master web publication)
    /// * Design Elements (if applicable)
    /// * Views (if applicable)
    /// </summary>
    [TcmTemplateTitle("Publish Version")]
    public class PublishVersion : TemplateBase
    {
        private const string BOOTSTRAP_FILENAME = "_all";
        private const string TEMPLATE_CONFIG_NAME = "templates";
        private const string SCHEMAS_CONFIG_NAME = "schemas";
        private const string TAXONOMIES_CONFIG_NAME = "taxonomies";
        private string moduleRoot = "";

        public override void Transform(Engine engine, Package package)
        {
            this.Initialize(engine, package);
            var folderWebDavUrl = this.GetPublication().RootFolder.WebDavUrl + "/" + package.GetValue("folderWebDavUrl");
            var sgUri = m_Package.GetValue("sg_TargetStructureGroup");
            var sg = (StructureGroup)m_Engine.GetObject(engine.LocalizeUri(new TcmUri(sgUri)));
            Folder folder = null;
            try
            {
                folder = (Folder)m_Engine.GetObject(folderWebDavUrl);
            }
            catch
            {
                throw new Exception(String.Format("Cannot find modules folder: {0}. Please check it exists and update your TBB parameters accordingly.",folderWebDavUrl));
            }
            moduleRoot = folder.WebDavUrl;
            if (sg == null)
            {
                throw new Exception(String.Format("Could not find structure group {0} in context publication {1}", sgUri, this.GetPublication().Title));
            }
            List<string> bootstrapFiles = new List<string>();
            bootstrapFiles.Add(Json.Encode(PublishAssets(folder, sg, "config")));
            bootstrapFiles.Add(Json.Encode(PublishAssets(folder, sg, "resources")));
            Item jsonBootstrap = m_Package.CreateStringItem(ContentType.Text, String.Format("{{\"files\":[{0}]}}", String.Join(",", bootstrapFiles)));
            m_Package.PushItem("Output", jsonBootstrap);
            m_Engine.PublishingContext.RenderedItem.AddBinary(jsonBootstrap.GetAsStream(), BOOTSTRAP_FILENAME + JSON_EXTENSION, sg, "bootstrap", this.GetComponent(), JSON_MIMETYPE);
        }

        private string PublishAssets(Folder rootFolder, StructureGroup rootSg, string type)
        {
            Folder folder = (Folder)GetChildOrganizationalItem(rootFolder, "_" + type);
            StructureGroup sg = (StructureGroup)GetChildOrganizationalItem(rootSg, type);
            if (folder == null)
            {
                throw new Exception(String.Format("Could not find Folder '_{0}' in module folder {1}", type, rootFolder.WebDavUrl));
            }
            if (sg == null)
            {
                throw new Exception(String.Format("Could not find Structure Group '{0}' in system Structure Group {1}", type, sg.WebDavUrl));
            }
            List<string> files = new List<string>();
            Component allComponent = null;
            foreach (var item in GetOrganizationalItemContents(folder, ItemType.Component, false))
            {
                var comp = (Component)m_Engine.GetObject(item.Key.ToString());
                var configType = item.Value.ToLower();
                if (configType == BOOTSTRAP_FILENAME)
                {
                    //There is a dummy _All component used to publish a bootstrap file containing a list of other files that have been generated
                    allComponent = comp;
                    continue;
                }
                var settings = new Dictionary<string, List<string>>();
                if (configType == TEMPLATE_CONFIG_NAME || configType == SCHEMAS_CONFIG_NAME || configType == TAXONOMIES_CONFIG_NAME)
                {
                    //template, schema and taxonomy config is only published from the master web publication/default localization
                    if (this.IsMasterWebPublication())
                    {
                        switch (configType)
                        {
                            case TEMPLATE_CONFIG_NAME:
                                settings = ReadTemplateData();
                                break;
                            case SCHEMAS_CONFIG_NAME:
                                settings = ReadSchemaData();
                                break;
                            case TAXONOMIES_CONFIG_NAME:
                                settings = ReadTaxonomiesData();
                                break;
                        }
                    }
                }
                else
                {
                    settings = new Dictionary<string,List<string>>();
                    settings.Add(configType, ReadComponentData(comp));
                }
                //Add all the JSON files to the package and publish them as binaries
                foreach (var key in settings.Keys)
                {
                    Item jsonItem = m_Package.CreateStringItem(ContentType.Text, String.Format("{{{0}}}", String.Join(",\n", settings[key])));
                    var filename = key + JSON_EXTENSION;
                    m_Package.PushItem(type + "/" + filename, jsonItem);
                    var binary = m_Engine.PublishingContext.RenderedItem.AddBinary(jsonItem.GetAsStream(), filename, sg, key, comp, JSON_MIMETYPE);
                    files.Add(Json.Encode(binary.Url));
                }
            }
            if (allComponent == null)
            {
                throw new Exception(String.Format("Cannot find '_All' component. Please ensure that this component is in the {0} folder.", folder.WebDavUrl));
            }
            //We create a special asset file _all.js which contains the path of all other asset files of this type that have been published
            Item filesItem = m_Package.CreateStringItem(ContentType.Text, String.Format("{{\"defaultLocalization\":{0},\"files\":[{1}]}}", Json.Encode(this.IsMasterWebPublication()), String.Join(",", files)));
            m_Package.PushItem(type + "/" + BOOTSTRAP_FILENAME + JSON_EXTENSION, filesItem);
            var bootstrapBinary = m_Engine.PublishingContext.RenderedItem.AddBinary(filesItem.GetAsStream(), BOOTSTRAP_FILENAME + JSON_EXTENSION, sg, type, allComponent, JSON_MIMETYPE);
            return bootstrapBinary.Url;
        }

        private List<string> ReadComponentData(Component comp)
        {
            var fields = new ItemFields(comp.Content, comp.Schema);
            var settings = new List<string>();
            var configFields = fields.GetEmbeddedFields("settings");
            if (configFields.Count() > 0)
            {
                //either schema is a generic multival embedded name/value
                foreach (var setting in configFields)
                {
                    settings.Add(String.Format("{0}:{1}", Json.Encode(setting.GetTextValue("name")), Json.Encode(setting.GetTextValue("value"))));
                }
            }
            else
            {
                //... or its a custom schema with individual fields
                foreach (var field in fields)
                {
                    //TODO handle more types
                    if (field is TextField)
                    {
                        settings.Add(String.Format("{0}:{1}", Json.Encode(field.Name), Json.Encode(fields.GetTextValue(field.Name))));
                    }
                }
            }
            return settings;
        }

        private Dictionary<string, List<string>> ReadTaxonomiesData()
        {
            //Generate a list of taxonomy + id
            var res = new Dictionary<string, List<string>>();
            var settings = new List<string>();
            var taxFilter = new TaxonomiesFilter(m_Engine.GetSession()) { BaseColumns = ListBaseColumns.Extended };
            foreach (XmlElement item in GetPublication().GetListTaxonomies(taxFilter).ChildNodes)
            {
                var id = item.GetAttribute("ID");
                var taxonomy = (Category)m_Engine.GetObject(id);
                settings.Add(String.Format("{0}:{1}", Json.Encode(GetKeyFromTaxonomy(taxonomy)), Json.Encode(taxonomy.Id.ItemId)));
            }
            res.Add("core." + TAXONOMIES_CONFIG_NAME, settings);
            return res;
        }

        private Dictionary<string, List<string>> ReadSchemaData()
        {
            //Generate a list of schema + id, separated by module
            var res = new Dictionary<string, List<string>>();
            var schemaFilter = new RepositoryItemsFilter(m_Engine.GetSession()) { Recursive = true };
            schemaFilter.ItemTypes = new List<ItemType> { ItemType.Schema };
            schemaFilter.BaseColumns = ListBaseColumns.Extended;
            foreach (XmlElement item in GetPublication().GetListItems(schemaFilter).ChildNodes)
            {
                var type = item.GetAttribute("Type");
                var subType = item.GetAttribute("SubType");
                //We only consider normal schemas (type=8 subtype=0)
                if ((type == "8" && subType == "0"))
                {
                    var id = item.GetAttribute("ID");
                    var schema = (Schema)m_Engine.GetObject(id);
                    var module = GetModuleName(schema.WebDavUrl);
                    if (module != null)
                    {
                        var key = module + "." + SCHEMAS_CONFIG_NAME;
                        if (!res.ContainsKey(key))
                        {
                            res.Add(key, new List<string>());
                        }
                        res[key].Add(String.Format("{0}:{1}", Json.Encode(GetKeyFromSchema(schema)), Json.Encode(schema.Id.ItemId)));
                    }
                }
            }
            return res;
        }

        private Dictionary<string, List<string>> ReadTemplateData()
        {
            //Generate a list of dynamic CT + id, separated by module
            var res = new Dictionary<string, List<string>>();
            var templateFilter = new ComponentTemplatesFilter(m_Engine.GetSession()) { BaseColumns = ListBaseColumns.Extended };
            foreach (XmlElement item in GetPublication().GetListComponentTemplates(templateFilter).ChildNodes)
            {
                var id = item.GetAttribute("ID");
                var template = (ComponentTemplate)m_Engine.GetObject(id);
                //Only consider dynamic CTs
                if (template.IsRepositoryPublishable)
                {
                    var module = GetModuleName(template.WebDavUrl);
                    if (module != null)
                    {
                        var key = module + "." + TEMPLATE_CONFIG_NAME;
                        if (!res.ContainsKey(key))
                        {
                            res.Add(key, new List<string>());
                        }
                        res[key].Add(String.Format("{0}:{1}", Json.Encode(GetKeyFromTemplate(template)), Json.Encode(template.Id.ItemId)));
                    }
                }
            }
            return res;
        }

        private string GetKeyFromTaxonomy(Category taxonomy)
        {
            var key = taxonomy.XmlName;
            return key.Substring(0, 1).ToLower() + key.Substring(1);
        }

        private string GetKeyFromTemplate(ComponentTemplate template)
        {
            var key = Regex.Replace(template.Title, @"[\[\]\s\.]", "");
            return key.Substring(0, 1).ToLower() + key.Substring(1);
        }

        private string GetKeyFromSchema(Schema schema)
        {
            var key = schema.RootElementName;
            return key.Substring(0, 1).ToLower() + key.Substring(1);
        }

        private string GetModuleName(string fullItemWebdavUrl)
        {
            //The module name is the name of the folder within the first level of the module root folder 
            //in which the item lives
            if (fullItemWebdavUrl.StartsWith(moduleRoot))
            {
                Logger.Debug(fullItemWebdavUrl + ":" + moduleRoot);
                var res = fullItemWebdavUrl.Substring(moduleRoot.Length + 1);
                var pos = res.IndexOf("/");
                Logger.Debug(res);
                return res.Substring(0, pos).ToLower();
            }
            return null;
        }

        
    }

}

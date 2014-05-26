using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Tridion.ContentManager;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Templating;
using System.Xml;
using System.Web.Helpers;
using System.Text.RegularExpressions;
using Tridion.ContentManager.Templating.Assembly;
using Sdl.Web.Templating.ExtensionMethods;

namespace Sdl.Web.Templating
{
    [TcmTemplateTitle("Publish Configuration")]
    public class PublishConfiguration : TemplateBase.TemplateBase
    {
        private const string TemplateConfigName = "templates";
        private const string SchemasConfigName = "schemas";
        private const string TaxonomiesConfigName = "taxonomies";
        private string _moduleRoot = string.Empty;

        public override void Transform(Engine engine, Package package)
        {
            this.Initialize(engine, package);
            //The core configuration component should be the one being processed by the template
            var coreConfigComponent = this.GetComponent();
            var sg = GetSystemStructureGroup("config");
            _moduleRoot = GetModulesRoot(coreConfigComponent);
            //Get all the active modules
            Dictionary<string, Component> moduleComponents = GetActiveModules(coreConfigComponent);
            List<string> filesCreated = new List<string>();
            //For each active module, publish the config and add the filename(s) to the bootstrap list
            foreach (var module in moduleComponents)
            {
                filesCreated.Add(ProcessModule(module.Key, module.Value, sg));
            }
            //template, schema and taxonomy config is only published from the master web publication/default localization
            if (IsMasterWebPublication())
            {
                filesCreated.AddRange(PublishJsonData(ReadSchemaData(), coreConfigComponent, sg));
                filesCreated.AddRange(PublishJsonData(ReadTemplateData(), coreConfigComponent, sg));
                filesCreated.AddRange(PublishJsonData(ReadTaxonomiesData(), coreConfigComponent, sg));
            }
            //Publish the boostrap list, this is used by the web application to load in all other configuration files
            PublishBootstrapJson(filesCreated, coreConfigComponent, sg, "config-");
        }



        protected string ProcessModule(string moduleName, Component module, StructureGroup sg)
        {
            List<string> data = new List<string>();
            ItemFields fields = new ItemFields(module.Content, module.Schema);
            foreach (var configComp in fields.GetComponentValues("furtherConfiguration"))
            {
                data.AddRange(ReadComponentData(configComp));
            }
            return PublishJsonData(data, module,moduleName, sg);
        }

        

        private Dictionary<string, List<string>> ReadTaxonomiesData()
        {
            //Generate a list of taxonomy + id
            var res = new Dictionary<string, List<string>>();
            var settings = new List<string>();
            var taxFilter = new TaxonomiesFilter(MEngine.GetSession()) { BaseColumns = ListBaseColumns.Extended };
            foreach (XmlElement item in GetPublication().GetListTaxonomies(taxFilter).ChildNodes)
            {
                var id = item.GetAttribute("ID");
                var taxonomy = (Category)MEngine.GetObject(id);
                settings.Add(String.Format("{0}:{1}", Json.Encode(GetKeyFromTaxonomy(taxonomy)), Json.Encode(taxonomy.Id.ItemId)));
            }
            res.Add("core." + TaxonomiesConfigName, settings);
            return res;
        }

        private Dictionary<string, List<string>> ReadSchemaData()
        {
            //Generate a list of schema + mapping details, separated by module
            var res = new Dictionary<string, List<string>>();
            var schemaFilter = new RepositoryItemsFilter(MEngine.GetSession())
            {
                Recursive = true,
                ItemTypes = new List<ItemType> { ItemType.Schema },
                BaseColumns = ListBaseColumns.Extended
            };
            foreach (XmlElement item in GetPublication().GetListItems(schemaFilter).ChildNodes)
            {
                var type = item.GetAttribute("Type");
                var subType = item.GetAttribute("SubType");
                //We only consider normal schemas (type=8 subtype=0)
                if ((type == "8" && subType == "0"))
                {
                    var id = item.GetAttribute("ID");
                    var schema = (Schema)MEngine.GetObject(id);
                    var module = GetModuleNameFromItem(schema, _moduleRoot);
                    if (module != null)
                    {
                        var key = module + "." + SchemasConfigName;
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
            var templateFilter = new ComponentTemplatesFilter(MEngine.GetSession()) { BaseColumns = ListBaseColumns.Extended };
            foreach (XmlElement item in GetPublication().GetListComponentTemplates(templateFilter).ChildNodes)
            {
                var id = item.GetAttribute("ID");
                var template = (ComponentTemplate)MEngine.GetObject(id);
                //Only consider dynamic CTs
                if (template.IsRepositoryPublishable)
                {
                    var module = GetModuleNameFromItem(template, _moduleRoot);
                    if (module != null)
                    {
                        var key = module + "." + TemplateConfigName;
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

        private static string GetKeyFromTaxonomy(Category taxonomy)
        {
            var key = taxonomy.XmlName;
            return key.Substring(0, 1).ToLower() + key.Substring(1);
        }

        private static string GetKeyFromTemplate(ComponentTemplate template)
        {
            var key = Regex.Replace(template.Title, @"[\[\]\s\.]", "");
            return key.Substring(0, 1).ToLower() + key.Substring(1);
        }

        private static string GetKeyFromSchema(Schema schema)
        {
            var key = schema.RootElementName;
            if (string.IsNullOrEmpty(key))
            {
                key = Regex.Replace(schema.Title.Trim(), @"[^A-Za-z0-9.]+", "");
            }
            return key.Substring(0, 1).ToLower() + key.Substring(1);
        }
    }
}

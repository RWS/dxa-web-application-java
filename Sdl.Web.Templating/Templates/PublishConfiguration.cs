using System;
using System.Collections.Generic;
using System.Web.Helpers;
using System.Xml;
using Sdl.Web.Tridion.Common;
using Tridion.ContentManager;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;

namespace Sdl.Web.Tridion.Templates
{
    /// <summary>
    /// Publishes site configuration as JSON files. Multiple configuration components can 
    /// be linked to a module configuration. The values in these are merged into a single
    /// configuration file per module. There are also JSON files published containing schema and template
    /// information (1 per module) and a general taxonomies configuration json file
    /// </summary>
    [TcmTemplateTitle("Publish Configuration")]
    public class PublishConfiguration : TemplateBase
    {
        private const string TemplateConfigName = "templates";
        private const string SchemasConfigName = "schemas";
        private const string TaxonomiesConfigName = "taxonomies";

        private string _moduleRoot = String.Empty;

        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);
            
            //The core configuration component should be the one being processed by the template
            var coreConfigComponent = GetComponent();
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
                filesCreated.AddRange(PublishJsonData(ReadSchemaData(), coreConfigComponent, "schemas", sg));
                filesCreated.AddRange(PublishJsonData(ReadTemplateData(), coreConfigComponent, "templates", sg));
                filesCreated.AddRange(PublishJsonData(ReadTaxonomiesData(), coreConfigComponent, "taxonomies", sg));
            }
            //Publish the boostrap list, this is used by the web application to load in all other configuration files
            PublishBootstrapJson(filesCreated, coreConfigComponent, sg, "config-", BuildAdditionalData());
        }

        protected virtual string ProcessModule(string moduleName, Component module, StructureGroup sg)
        {
            Dictionary<string, string> data = new Dictionary<string, string>();
            ItemFields fields = new ItemFields(module.Content, module.Schema);
            foreach (var configComp in fields.GetComponentValues("furtherConfiguration"))
            {
                data = MergeData(data, ReadComponentData(configComp));
            }
            return PublishJsonData(data, module, moduleName, "config", sg);
        }

        protected virtual Dictionary<string, List<string>> ReadTaxonomiesData()
        {
            //Generate a list of taxonomy + id
            var res = new Dictionary<string, List<string>>();
            var settings = new List<string>();
            var taxFilter = new TaxonomiesFilter(Engine.GetSession()) { BaseColumns = ListBaseColumns.Extended };
            foreach (XmlElement item in GetPublication().GetListTaxonomies(taxFilter).ChildNodes)
            {
                var id = item.GetAttribute("ID");
                var taxonomy = (Category)Engine.GetObject(id);
                settings.Add(String.Format("{0}:{1}", Json.Encode(Utility.GetKeyFromTaxonomy(taxonomy)), Json.Encode(taxonomy.Id.ItemId)));
            }
            res.Add("core." + TaxonomiesConfigName, settings);
            return res;
        }

        protected virtual Dictionary<string, List<string>> ReadSchemaData()
        {
            //Generate a list of schema + mapping details, separated by module
            var res = new Dictionary<string, List<string>>();
            var schemaFilter = new RepositoryItemsFilter(Engine.GetSession())
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
                    var schema = (Schema)Engine.GetObject(id);
                    var module = GetModuleNameFromItem(schema, _moduleRoot);
                    if (module != null)
                    {
                        var key = module + "." + SchemasConfigName;
                        if (!res.ContainsKey(key))
                        {
                            res.Add(key, new List<string>());
                        }
                        res[key].Add(String.Format("{0}:{1}", Json.Encode(Utility.GetKeyFromSchema(schema)), Json.Encode(schema.Id.ItemId)));
                    }
                }
            }
            return res;
        }

        protected virtual Dictionary<string, List<string>> ReadTemplateData()
        {
            //Generate a list of dynamic CT + id, separated by module
            var res = new Dictionary<string, List<string>>();
            var templateFilter = new ComponentTemplatesFilter(Engine.GetSession()) { BaseColumns = ListBaseColumns.Extended };
            foreach (XmlElement item in GetPublication().GetListComponentTemplates(templateFilter).ChildNodes)
            {
                var id = item.GetAttribute("ID");
                var template = (ComponentTemplate)Engine.GetObject(id);
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
                        res[key].Add(String.Format("{0}:{1}", Json.Encode(Utility.GetKeyFromTemplate(template)), Json.Encode(template.Id.ItemId)));
                    }
                }
            }
            return res;
        }

        protected List<string> BuildAdditionalData()
        {
            //Some additional data required to configure the web application
            List<string> additionalData = new List<string>
                {
                    String.Format("\"defaultLocalization\":{0}", Json.Encode(IsMasterWebPublication())),
                    String.Format("\"staging\":{0}", Json.Encode(IsPublishingToStaging())),
                    String.Format("\"mediaRoot\":{0}", Json.Encode(GetPublication().MultimediaUrl))
                };
            return additionalData;
        }

    }
}

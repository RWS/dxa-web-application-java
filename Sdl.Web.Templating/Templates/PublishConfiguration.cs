using System;
using System.Collections.Generic;
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
            filesCreated.AddRange(PublishJsonData(ReadSchemaData(), coreConfigComponent, "schemas", sg));
            filesCreated.AddRange(PublishJsonData(ReadTemplateData(), coreConfigComponent, "templates", sg));
            filesCreated.AddRange(PublishJsonData(ReadTaxonomiesData(), coreConfigComponent, "taxonomies", sg));
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
                settings.Add(String.Format("{0}:{1}", JsonEncode(Utility.GetKeyFromTaxonomy(taxonomy)), JsonEncode(taxonomy.Id.ItemId)));
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
                        res[key].Add(String.Format("{0}:{1}", JsonEncode(Utility.GetKeyFromSchema(schema)), JsonEncode(schema.Id.ItemId)));
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
                        res[key].Add(String.Format("{0}:{1}", JsonEncode(Utility.GetKeyFromTemplate(template)), JsonEncode(template.Id.ItemId)));
                    }
                }
            }
            return res;
        }

        protected List<string> BuildAdditionalData()
        {
            List<string> additionalData = new List<string>
                {
                    String.Format("\"defaultLocalization\":{0}", JsonEncode(IsMasterWebPublication())),
                    String.Format("\"staging\":{0}", JsonEncode(IsPublishingToStaging())),
                    String.Format("\"mediaRoot\":{0}", JsonEncode(GetPublication().MultimediaUrl)),
                    String.Format("\"siteLocalizations\":{0}", JsonEncode(LoadSitePublications(GetPublication())))
                };
            return additionalData;
        }

        private Publication GetMasterPublication(Publication contextPublication, ref string siteId)
        {
            siteId = GetSiteIdFromPublication(contextPublication) ?? siteId;
            List<Publication> validParents = new List<Publication>();
            if (siteId != null)
            {
                foreach (var item in contextPublication.Parents)
                {
                    Publication parent = (Publication)item;
                    if (IsCandidateMaster(parent, siteId))
                    {
                        validParents.Add(parent);
                    }
                }
            }
            if (validParents.Count > 1)
            {
                Logger.Error(String.Format("Publication {0} has more than one parent with the same (or empty) siteId {1}. Cannot determine site grouping, so picking the first parent: {2}.", contextPublication.Title, siteId, validParents[0].Title));
            }
            return validParents.Count==0 ? contextPublication : GetMasterPublication(validParents[0], ref siteId);
        }

        private bool IsCandidateMaster(Publication pub, string siteId)
        {
            //A publication is a valid master its siteId matches the passed siteId
            //Or the passed siteId is null
            var parentSiteId = GetSiteIdFromPublication(pub);
            if (siteId!=null && siteId!=parentSiteId)
            {
                return false;
            }
            return true;
        }


        private List<string> LoadSitePublications(Publication contextPublication)
        {
            string siteId = null;
            var master = GetMasterPublication(contextPublication, ref siteId);
            Logger.Debug(String.Format("Master publication is : {0}, siteId is {1}", master.Title, siteId));
            List<string> pubIds = new List<string> { master.Id.ItemId.ToString() };
            if (siteId!=null)
            {
                pubIds.AddRange(GetChildPublicationIds(master, siteId));
            }
            return pubIds;
        }

        private List<string> GetChildPublicationIds(Publication master, string siteId)
        {
            List<string> pubIds = new List<string>();
            var filter = new UsingItemsFilter(Engine.GetSession()) { ItemTypes = new List<ItemType> { ItemType.Publication } };
            foreach (XmlElement item in master.GetListUsingItems(filter).ChildNodes)
            {
                var id = item.GetAttribute("ID");
                var child = (Publication)Engine.GetObject(id);
                var childSiteId = GetSiteIdFromPublication(child);
                if (childSiteId == siteId)
                {
                    Logger.Debug(String.Format("Found valid descendent {0} with site ID {1} ", child.Title, childSiteId));
                    pubIds.Add(child.Id.ItemId.ToString());
                    if (child.HasChildren)
                    {
                        pubIds.AddRange(GetChildPublicationIds(child, siteId));
                    }
                }
                else
                {
                    Logger.Debug(String.Format("Descendent {0} has invalid site ID {1} - ignoring ",child.Title,childSiteId));
                }
            }
            return pubIds;
        }

        private string GetSiteIdFromPublication(Publication startPublication)
        {
            if (startPublication.Metadata!=null)
            {
                var meta = new ItemFields(startPublication.Metadata, startPublication.MetadataSchema);
                return meta.GetTextValue("siteId");
            }
            return null;
        }
    }
}

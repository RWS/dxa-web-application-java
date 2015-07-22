using System.Globalization;
using System.Text;
using Sdl.Web.Tridion.Common;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml;
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
        // template builder log
        private static readonly TemplatingLogger Log = TemplatingLogger.GetLogger(typeof(PublishStaticBootstrap));

        // json content in page
        private const string JsonOutputFormat = "{{\"name\":\"Publish Configuration\",\"status\":\"Success\",\"files\":[{0}]}}";

        private const string TemplateConfigName = "templates";
        private const string SchemasConfigName = "schemas";
        private const string TaxonomiesConfigName = "taxonomies";

        private string _moduleRoot = String.Empty;
        private Component _localizationConfigurationComponent;

        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);
            
            //The core configuration component should be the one being processed by the template
            Component coreConfigComponent = GetComponent();
            StructureGroup sg = GetSystemStructureGroup("config");
            _moduleRoot = GetModulesRoot(coreConfigComponent);

            //Get all the active modules
            Dictionary<string, Component> moduleComponents = GetActiveModules(coreConfigComponent);
            List<string> filesCreated = new List<string>();
            
            //For each active module, publish the config and add the filename(s) to the bootstrap list
            foreach (KeyValuePair<string, Component> module in moduleComponents)
            {
                filesCreated.Add(ProcessModule(module.Key, module.Value, sg));
            }
            filesCreated.AddRange(PublishJsonData(ReadSchemaData(), coreConfigComponent, "schemas", sg));
            filesCreated.AddRange(PublishJsonData(ReadTemplateData(), coreConfigComponent, "templates", sg));
            filesCreated.AddRange(PublishJsonData(ReadTaxonomiesData(), coreConfigComponent, "taxonomies", sg));
            
            //Publish the boostrap list, this is used by the web application to load in all other configuration files
            PublishBootstrapJson(filesCreated, coreConfigComponent, sg, "config-", BuildAdditionalData());

            StringBuilder publishedFiles = new StringBuilder();
            foreach (string file in filesCreated)
            {
                if (!String.IsNullOrEmpty(file))
                {
                    publishedFiles.AppendCommaSeparated(file);
                    Log.Info("Published " + file);
                }
            }

            // append json result to output
            string output = String.Format(JsonOutputFormat, publishedFiles);
            Item outputItem = package.GetByName(Package.OutputName);
            if (outputItem != null)
            {
                package.Remove(outputItem);
                // TODO: don't just blindly append to the previous output but generate valid json (note: it is only there for preview)
                output = outputItem.GetAsString() + Environment.NewLine + output;
            }
            package.PushItem(Package.OutputName, package.CreateStringItem(ContentType.Text, output));
        }

        protected virtual string ProcessModule(string moduleName, Component module, StructureGroup sg)
        {
            Dictionary<string, string> data = new Dictionary<string, string>();
            ItemFields fields = new ItemFields(module.Content, module.Schema);
            foreach (Component configComp in fields.GetComponentValues("furtherConfiguration"))
            {
                data = MergeData(data, ReadComponentData(configComp));
                if (configComp.Title == "Localization Configuration")
                {
                    _localizationConfigurationComponent = configComp;
                }
            }
            return PublishJsonData(data, module, moduleName, "config", sg);
        }

        protected virtual Dictionary<string, List<string>> ReadTaxonomiesData()
        {
            //Generate a list of taxonomy + id
            Dictionary<string, List<string>> res = new Dictionary<string, List<string>>();
            List<string> settings = new List<string>();
            TaxonomiesFilter taxFilter = new TaxonomiesFilter(Engine.GetSession()) { BaseColumns = ListBaseColumns.Extended };
            foreach (XmlElement item in GetPublication().GetListTaxonomies(taxFilter).ChildNodes)
            {
                string id = item.GetAttribute("ID");
                Category taxonomy = (Category)Engine.GetObject(id);
                settings.Add(String.Format("{0}:{1}", JsonEncode(Utility.GetKeyFromTaxonomy(taxonomy)), JsonEncode(taxonomy.Id.ItemId)));
            }
            res.Add("core." + TaxonomiesConfigName, settings);
            return res;
        }

        protected virtual Dictionary<string, List<string>> ReadSchemaData()
        {
            //Generate a list of schema + mapping details, separated by module
            Dictionary<string, List<string>> res = new Dictionary<string, List<string>>();
            RepositoryItemsFilter schemaFilter = new RepositoryItemsFilter(Engine.GetSession())
            {
                Recursive = true,
                ItemTypes = new List<ItemType> { ItemType.Schema },
                BaseColumns = ListBaseColumns.Extended
            };
            foreach (XmlElement item in GetPublication().GetListItems(schemaFilter).ChildNodes)
            {
                string type = item.GetAttribute("Type");
                string subType = item.GetAttribute("SubType");
                //We only consider normal schemas (type=8 subtype=0)
                if ((type == "8" && subType == "0"))
                {
                    string id = item.GetAttribute("ID");
                    Schema schema = (Schema)Engine.GetObject(id);
                    string module = GetModuleNameFromItem(schema, _moduleRoot);
                    if (module != null)
                    {
                        string key = module + "." + SchemasConfigName;
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
            Dictionary<string, List<string>> res = new Dictionary<string, List<string>>();
            ComponentTemplatesFilter templateFilter = new ComponentTemplatesFilter(Engine.GetSession()) { BaseColumns = ListBaseColumns.Extended };
            foreach (XmlElement item in GetPublication().GetListComponentTemplates(templateFilter).ChildNodes)
            {
                string id = item.GetAttribute("ID");
                ComponentTemplate template = (ComponentTemplate)Engine.GetObject(id);
                //Only consider dynamic CTs
                if (template.IsRepositoryPublishable)
                {
                    string module = GetModuleNameFromItem(template, _moduleRoot);
                    if (module != null)
                    {
                        string key = module + "." + TemplateConfigName;
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
            if (_localizationConfigurationComponent == null)
            {
                Logger.Warning("Could not find 'Localization Configuration' component, cannot publish language data");
            }
            IEnumerable<PublicationDetails> sitePubs = LoadSitePublications(GetPublication());
            bool isMaster = sitePubs.Where(p => p.Id == GetPublication().Id.ItemId.ToString(CultureInfo.InvariantCulture)).FirstOrDefault().IsMaster;
            List<string> additionalData = new List<string>
                {
                    String.Format("\"defaultLocalization\":{0}", JsonEncode(isMaster)),
                    String.Format("\"staging\":{0}", JsonEncode(IsPublishingToStaging())),
                    String.Format("\"mediaRoot\":{0}", JsonEncode(GetPublication().MultimediaUrl)),
                    String.Format("\"siteLocalizations\":{0}", JsonEncode(sitePubs))
                };
            return additionalData;
        }

        private Publication GetMasterPublication(Publication contextPublication)
        {
            string siteId = GetSiteIdFromPublication(contextPublication);
            List<Publication> validParents = new List<Publication>();
            if (siteId != null && siteId!="multisite-master")
            {
                foreach (Repository item in contextPublication.Parents)
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
            return validParents.Count==0 ? contextPublication : GetMasterPublication(validParents[0]);
        }

        private bool IsCandidateMaster(Publication pub, string childId)
        {
            //A publication is a valid master if:
            //a) Its siteId is "multisite-master" or
            //b) Its siteId matches the passed (child) siteId
            string siteId = GetSiteIdFromPublication(pub);
            return siteId == "multisite-master" || childId == siteId;
        }


        private IEnumerable<PublicationDetails> LoadSitePublications(Publication contextPublication)
        {
            string siteId = GetSiteIdFromPublication(contextPublication);
            Publication master = GetMasterPublication(contextPublication);
            Logger.Debug(String.Format("Master publication is : {0}, siteId is {1}", master.Title, siteId));
            List<PublicationDetails> pubs = new List<PublicationDetails>();
            bool masterAdded = false;
            if (GetSiteIdFromPublication(master) == siteId)
            {
                masterAdded = IsMasterWebPublication(master);
                pubs.Add(GetPublicationDetails(master, masterAdded));
            }
            if (siteId!=null)
            {
                pubs.AddRange(GetChildPublicationDetails(master, siteId, masterAdded));
            }
            //It is possible that no publication has been set explicitly as the master
            //in which case we set the context publication as the master
            if (!pubs.Any(p => p.IsMaster))
            {
                string currentPubId = GetPublication().Id.ItemId.ToString(CultureInfo.InvariantCulture);
                foreach (PublicationDetails pub in pubs)
                {
                    if (pub.Id==currentPubId)
                    {
                        pub.IsMaster = true;
                    }
                }
            }
            return pubs;
        }

        private PublicationDetails GetPublicationDetails(Publication pub, bool isMaster = false)
        {
            PublicationDetails pubData = new PublicationDetails { Id = pub.Id.ItemId.ToString(CultureInfo.InvariantCulture), Path = pub.PublicationUrl, IsMaster = isMaster};
            if (_localizationConfigurationComponent != null)
            {
                TcmUri localUri = new TcmUri(_localizationConfigurationComponent.Id.ItemId,ItemType.Component,pub.Id.ItemId);
                Component locComp = (Component)Engine.GetObject(localUri);
                if (locComp != null)
                {
                    ItemFields fields = new ItemFields(locComp.Content, locComp.Schema);
                    foreach (ItemFields field in fields.GetEmbeddedFields("settings"))
                    {
                        if (field.GetTextValue("name") == "language")
                        {
                            pubData.Language = field.GetTextValue("value");
                            break;
                        }
                    }
                }
            }
            return pubData;
        }

        private IEnumerable<PublicationDetails> GetChildPublicationDetails(Publication master, string siteId, bool masterAdded)
        {
            List<PublicationDetails> pubs = new List<PublicationDetails>();
            UsingItemsFilter filter = new UsingItemsFilter(Engine.GetSession()) { ItemTypes = new List<ItemType> { ItemType.Publication } };
            foreach (XmlElement item in master.GetListUsingItems(filter).ChildNodes)
            {
                string id = item.GetAttribute("ID");
                Publication child = (Publication)Engine.GetObject(id);
                string childSiteId = GetSiteIdFromPublication(child);
                if (childSiteId == siteId)
                {
                    Logger.Debug(String.Format("Found valid descendent {0} with site ID {1} ", child.Title, childSiteId));
                    bool isMaster = !masterAdded && IsMasterWebPublication(child);
                    pubs.Add(GetPublicationDetails(child, isMaster));
                    masterAdded = masterAdded || isMaster;
                }
                else
                {
                    Logger.Debug(String.Format("Descendent {0} has invalid site ID {1} - ignoring ",child.Title,childSiteId));
                }
            }
            return pubs;
        }

        private string GetSiteIdFromPublication(Publication startPublication)
        {
            if (startPublication.Metadata!=null)
            {
                ItemFields meta = new ItemFields(startPublication.Metadata, startPublication.MetadataSchema);
                return meta.GetTextValue("siteId");
            }
            return null;
        }
    }

    internal class PublicationDetails
    {
        public string Id { get; set; }
        public string Path { get; set; }
        public string Language { get; set; }
        public bool IsMaster { get; set; }
    }
}

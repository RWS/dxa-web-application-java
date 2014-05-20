using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Web.Helpers;
using System.Xml;
using System.Xml.Linq;
using Sdl.Web.Templating.ExtensionMethods;
using Tridion.ContentManager;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;

namespace Sdl.Web.Templating
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
    [TcmTemplateParameterSchema("resource:Sdl.Web.Templating.Resources.Schemas.Publish Version Parameters.xsd")]
    public class PublishVersion : TemplateBase.TemplateBase
    {
        private const string BootstrapFilename = "_all";
        private const string TemplateConfigName = "templates";
        private const string SchemasConfigName = "schemas";
        private const string TaxonomiesConfigName = "taxonomies";
        private const string MappingsConfigName = "mappings";
        private const string RegionConfigName = "regions";
        private const string VocabulariesConfigName = "vocabularies";
        private const string VocabulariesAppDataId = "http://www.sdl.com/tridion/SemanticMapping/vocabularies";
        private const string TypeOfAppDataId = "http://www.sdl.com/tridion/SemanticMapping/typeof";
        private const string DefaultVocabularyPrefix = "tsi";
        private const string DefaultVocabulary = "http://www.sdl.com/web/schemas/core";

        // list of known namespaces that are used in our schemas
        private readonly Dictionary<string, string> _namespaces = new Dictionary<string, string>
            {
                {Tridion.Constants.XsdPrefix, Tridion.Constants.XsdNamespace},
                {Tridion.Constants.TcmPrefix, Tridion.Constants.TcmNamespace},
                {Tridion.Constants.XlinkPrefix, Tridion.Constants.XlinkNamespace},
                {Tridion.Constants.XhtmlPrefix, Tridion.Constants.XhtmlNamespace},
                {"tcmi","http://www.tridion.com/ContentManager/5.0/Instance"},
                {"mapping", "http://www.sdl.com/tridion/SemanticMapping"}
            };

        private string _moduleRoot = string.Empty;

        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);
            var folderWebDavUrl = GetPublication().RootFolder.WebDavUrl + "/" + package.GetValue("folderWebDavUrl");
            var sgUri = MPackage.GetValue("sg_TargetStructureGroup");
            var sg = (StructureGroup)MEngine.GetObject(engine.LocalizeUri(new TcmUri(sgUri)));
            Folder folder;
            try
            {
                folder = (Folder)MEngine.GetObject(folderWebDavUrl);
            }
            catch
            {
                throw new Exception(String.Format("Cannot find modules folder: {0}. Please check it exists and update your TBB parameters accordingly.", folderWebDavUrl));
            }
            _moduleRoot = folder.WebDavUrl;
            if (sg == null)
            {
                throw new Exception(String.Format("Could not find structure group {0} in context publication {1}", sgUri, GetPublication().Title));
            }
            List<string> bootstrapFiles = new List<string>
                {
                    Json.Encode(PublishAssets(folder, sg, "config")),
                    Json.Encode(PublishAssets(folder, sg, "mappings")),
                    Json.Encode(PublishAssets(folder, sg, "resources"))
                };
            Item jsonBootstrap = MPackage.CreateStringItem(ContentType.Text, String.Format("{{\"files\":[{0}]}}", String.Join(",", bootstrapFiles)));
            MPackage.PushItem(Package.OutputName, jsonBootstrap);
            MEngine.PublishingContext.RenderedItem.AddBinary(jsonBootstrap.GetAsStream(), BootstrapFilename + JsonExtension, sg, "bootstrap", GetComponent(), JsonMimetype);
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
            bool isArray = false;
            foreach (var item in GetOrganizationalItemContents(folder, ItemType.Component, false))
            {
                var comp = (Component)MEngine.GetObject(item.Key.ToString());
                var configType = item.Value.ToLower();
                if (configType == BootstrapFilename)
                {
                    //There is a dummy _All component used to publish a bootstrap file containing a list of other files that have been generated
                    allComponent = comp;
                    continue;
                }
                var settings = new Dictionary<string, List<string>>();
                if (configType.Equals(TemplateConfigName) || configType.Equals(SchemasConfigName) || configType.Equals(TaxonomiesConfigName) || configType.Equals(MappingsConfigName))
                {
                    //template, schema and taxonomy config is only published from the master web publication/default localization
                    if (IsMasterWebPublication())
                    {
                        switch (configType)
                        {
                            case TemplateConfigName:
                                isArray = false;
                                settings = ReadTemplateData();
                                break;
                            case SchemasConfigName:
                                isArray = false;
                                settings = ReadSchemaData();
                                break;
                            case TaxonomiesConfigName:
                                isArray = false;
                                settings = ReadTaxonomiesData();
                                break;
                            case MappingsConfigName:
                                isArray = true;
                                settings = ReadMappingsData();
                                break;
                        }
                    }
                }
                else
                {
                    settings = new Dictionary<string, List<string>> { { configType, ReadComponentData(comp) } };
                }
                //Add all the JSON files to the package and publish them as binaries
                foreach (var key in settings.Keys)
                {
                    Item jsonItem;
                    if (isArray)
                    {
                        jsonItem = MPackage.CreateStringItem(ContentType.Text, String.Format("[{0}]", String.Join(",\n", settings[key])));
                    }
                    else
                    {
                        jsonItem = MPackage.CreateStringItem(ContentType.Text, String.Format("{{{0}}}", String.Join(",\n", settings[key])));
                    }
                    var filename = key + JsonExtension;
                    MPackage.PushItem(type + "/" + filename, jsonItem);
                    var binary = MEngine.PublishingContext.RenderedItem.AddBinary(jsonItem.GetAsStream(), filename, sg, key, comp, JsonMimetype);
                    files.Add(Json.Encode(binary.Url));
                }
            }
            if (allComponent == null)
            {
                throw new Exception(String.Format("Cannot find '_All' component. Please ensure that this component is in the {0} folder.", folder.WebDavUrl));
            }
            //We create a special asset file _all.js which contains the path of all other asset files of this type that have been published
            Item filesItem = MPackage.CreateStringItem(ContentType.Text, String.Format("{{\"defaultLocalization\":{0},\"staging\":{1},\"files\":[{2}]}}", Json.Encode(IsMasterWebPublication()), Json.Encode(IsPublishingToStaging()), String.Join(",", files)));
            MPackage.PushItem(type + "/" + BootstrapFilename + JsonExtension, filesItem);
            var bootstrapBinary = MEngine.PublishingContext.RenderedItem.AddBinary(filesItem.GetAsStream(), BootstrapFilename + JsonExtension, sg, type, allComponent, JsonMimetype);
            return bootstrapBinary.Url;
        }

        private static List<string> ReadComponentData(Component comp)
        {
            var fields = new ItemFields(comp.Content, comp.Schema);
            var settings = new List<string>();
            var configFields = fields.GetEmbeddedFields("settings");
            if (configFields.Any())
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

        private Dictionary<string, List<string>> ReadMappingsData()
        {
            bool containsDefaultVocabulary = false;

            // generate a list of vocabulary prefix and name from appdata
            var res = new Dictionary<string, List<string>> { { VocabulariesConfigName, new List<string>() } };
            ApplicationData globalAppData = MEngine.GetSession().SystemManager.LoadGlobalApplicationData(VocabulariesAppDataId);
            if (globalAppData != null)
            {
                XElement vocabulariesXml = XElement.Parse(Encoding.Unicode.GetString(globalAppData.Data));
                foreach (var vocabulary in vocabulariesXml.Elements())
                {
                    string prefix = vocabulary.Attribute("prefix").Value;
                    res[VocabulariesConfigName].Add(String.Format("{{\"Prefix\":{0},\"Vocab\":{1}}}", Json.Encode(prefix), Json.Encode(vocabulary.Attribute("name").Value)));
                    if (prefix.Equals(DefaultVocabularyPrefix))
                    {
                        containsDefaultVocabulary = true;
                    }
                }
            }
            // add default vocabulary if it is not there already
            if (!containsDefaultVocabulary)
            {
                res[VocabulariesConfigName].Add(String.Format("{{\"Prefix\":{0},\"Vocab\":{1}}}", Json.Encode(DefaultVocabularyPrefix), Json.Encode(DefaultVocabulary)));
            }

            // generate a list of schema + id, separated by module
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
                // we consider normal schemas (type=8 subtype=0) and multimedia schemas (type=8 subtype=1)
                if ((type == "8" && (subType == "0" || subType == "1")))
                {
                    var id = item.GetAttribute("ID");
                    var schema = (Schema)MEngine.GetObject(id);
                    var module = GetModuleName(schema.WebDavUrl);
                    if (module != null)
                    {
                        var key = module + "." + SchemasConfigName;
                        if (!res.ContainsKey(key))
                        {
                            res.Add(key, new List<string>());
                        }

                        // multimedia schemas don't have a root element name, so lets use its title without any invalid characters
                        string rootElementName = schema.RootElementName;
                        if (string.IsNullOrEmpty(rootElementName))
                        {
                            rootElementName = Regex.Replace(schema.Title.Trim(), @"[^A-Za-z0-9.]+", "");
                        }

                        // add schema typeof using tridion standard implementation vocabulary prefix
                        string typeOf = string.Format("{0}:{1}", DefaultVocabularyPrefix, rootElementName);
                        StringBuilder schemaSemantics = new StringBuilder();
                        // append schema typeof from appdata 
                        ApplicationData appData = schema.LoadApplicationData(TypeOfAppDataId);
                        if (appData != null)
                        {
                            typeOf += "," + Encoding.Unicode.GetString(appData.Data);
                        }
                        schemaSemantics.Append(BuildSchemaSemanticsJson(typeOf));

                        // TODO: serialize schema fields xml to json in a smart way
                        // field: {"Name":"something","Path":"/something","IsMultiValue":true,"Semantics":[],"Fields":[]}
                        // field semantics: {"Prefix":"s","Entity";"Article","Property":"headline"}
                        StringBuilder fields = new StringBuilder();

                        // load namespace manager with schema namespaces
                        XmlNamespaceManager nsmgr = SchemaNamespaceManager(schema.Xsd.OwnerDocument.NameTable);

                        // build field elements from schema
                        string path = "/" + rootElementName;
                        fields.Append(BuildSchemaFieldsJson(schema, path, typeOf, nsmgr));

                        res[key].Add(string.Format("{{\"Id\":{0},\"RootElement\":{1},\"Fields\":[{2}],\"Semantics\":[{3}]}}", Json.Encode(schema.Id.ItemId), Json.Encode(rootElementName), fields, schemaSemantics));
                    }
                }
            }

            // get region mappings for all templates
            Dictionary<string, Dictionary<string, List<string>>> modules = BuildRegionMappings();

            // append region mappings 
            foreach (var module in modules)
            {
                if (!res.ContainsKey(module.Key))
                {
                    res.Add(module.Key, new List<string>());
                }

                StringBuilder allowedComponentTypes = new StringBuilder();
                bool firstInRegion = true;
                foreach (var region in modules[module.Key])
                {
                    if (firstInRegion)
                    {
                        firstInRegion = false;
                    }
                    else
                    {
                        allowedComponentTypes.Append(",");
                    }

                    bool first = true; 
                    foreach (var componentType in region.Value)
                    {
                        if (first)
                        {
                            first = false;
                        }
                        else
                        {
                            allowedComponentTypes.Append(",");
                        }
                        allowedComponentTypes.Append(componentType);
                    }
                    res[module.Key].Add(string.Format("{{{0}:[{1}]}}", Json.Encode(region.Key), allowedComponentTypes));
                }
            }

            return res;
        }

        private Dictionary<string, Dictionary<string, List<string>>> BuildRegionMappings()
        {
            // format: module { region { schema, template } }
            Dictionary<string, Dictionary<string, List<string>>> modules = new Dictionary<string, Dictionary<string, List<string>>>();
             
            var templateFilter = new ComponentTemplatesFilter(MEngine.GetSession()) { BaseColumns = ListBaseColumns.Extended };
            foreach (XmlElement item in GetPublication().GetListComponentTemplates(templateFilter).ChildNodes)
            {
                var id = item.GetAttribute("ID");
                var template = (ComponentTemplate)MEngine.GetObject(id);
                var region = GetRegionFromTemplate(template);
                var module = GetModuleName(template.WebDavUrl);
                if (module != null)
                {
                    var key = module + "." + RegionConfigName;
                    if (!modules.ContainsKey(key))
                    {
                        modules.Add(key, new Dictionary<string, List<string>>());
                    }
                    if (!modules[key].ContainsKey(region))
                    {
                        modules[key].Add(region, new List<string>());
                    }

                    StringBuilder allowedComponentTypes = new StringBuilder();
                    bool first = true;
                    foreach (var schema in template.RelatedSchemas)
                    {
                        if (first)
                        {
                            first = false;
                        }
                        else
                        {
                            allowedComponentTypes.Append(",");
                        }
                        allowedComponentTypes.AppendFormat("{{\"schema\":{0},\"template\":{1}}}", Json.Encode(schema.Id.GetVersionlessUri().ToString()), Json.Encode(template.Id.GetVersionlessUri().ToString()));
                    }

                    // do not append empty strings (template.RelatedSchemas can be empty)
                    if (allowedComponentTypes.Length > 0)
                    {
                        modules[key][region].Add(allowedComponentTypes.ToString());                        
                    }
                }
            }

            return modules;
        }

        private XmlNamespaceManager SchemaNamespaceManager(XmlNameTable nameTable)
        {
            // load namespace manager with schema namespaces
            XmlNamespaceManager nsmgr = new XmlNamespaceManager(nameTable);
            foreach (var item in _namespaces)
            {
                nsmgr.AddNamespace(item.Key, item.Value);
            }
            return nsmgr;
        }

        // field: {"Name":"something","Path":"/something","IsMultiValue":true,"Semantics":[],"Fields":[]}
        // field semantics: {"Prefix":"s","Entity":"Article","Property":"headline"}
        private string BuildSchemaFieldsJson(Schema schema, string parentPath, string typeOf, XmlNamespaceManager nsmgr, bool embedded = false)
        {
            StringBuilder fields = new StringBuilder();

            // loop over all field elements in schema
            bool first = true;
            string xpath = string.Format("/xsd:schema/xsd:element[@name='{0}']/xsd:complexType/xsd:sequence/xsd:element", schema.RootElementName);
            if (embedded)
            {
                xpath = string.Format("/xsd:schema/xsd:complexType[@name='{0}']/xsd:sequence/xsd:element", schema.RootElementName);
            }
            foreach (XmlNode fieldNode in schema.Xsd.SelectNodes(xpath, nsmgr))
            {
                string fieldJson = BuildFieldJson(fieldNode, parentPath, typeOf, nsmgr);
                if (first)
                {
                    first = false;
                }
                else
                {
                    fields.Append(",");
                }
                fields.Append(fieldJson);
            }

            // embedded schemas do not contain metadata fields
            if (!embedded)
            {
                // add metadata fields
                xpath = "/xsd:schema/xsd:element[@name='Metadata']/xsd:complexType/xsd:sequence/xsd:element";
                foreach (XmlNode fieldNode in schema.Xsd.SelectNodes(xpath, nsmgr))
                {
                    // change last item in parentPath to Metadata
                    int index = parentPath.LastIndexOf('/');
                    if (index != -1)
                    {
                        parentPath = parentPath.Substring(0, index) + "/Metadata";
                    }

                    string fieldJson = BuildFieldJson(fieldNode, parentPath, typeOf, nsmgr);
                    if (first)
                    {
                        first = false;
                    }
                    else
                    {
                        fields.Append(",");
                    }
                    fields.Append(fieldJson);
                }                
            }

            return fields.ToString();
        }

        // field: {"Name":"something","Path":"/something","IsMultiValue":true,"Semantics":[],"Fields":[]}
        private string BuildFieldJson(XmlNode fieldNode, string parentPath, string typeOf, XmlNamespaceManager nsmgr)
        {
            string name = fieldNode.Attributes["name"].Value;
            string path = parentPath + "/" + name;
            string fieldTypeOf = typeOf;
            StringBuilder fieldSemantics = new StringBuilder();

            // if maxOccurs is anything else than 1, it is a multi value field
            bool isMultiValue = !fieldNode.Attributes["maxOccurs"].Value.Equals("1");

            // read semantic mapping from field so we can append it to the schema typeof
            XmlNode typeOfNode = fieldNode.SelectSingleNode("xsd:annotation/xsd:appinfo/tcm:ExtensionXml/mapping:typeof", nsmgr);
            if (typeOfNode != null)
            {
                fieldTypeOf = typeOf + "," + typeOfNode.InnerText;
            }

            // use field xml name as initial semantic mapping for field
            string property = string.Format("{0}:{1}", DefaultVocabularyPrefix, fieldNode.Attributes["name"].Value);

            // read semantic mapping from field and append if available
            XmlNode propertyNode = fieldNode.SelectSingleNode("xsd:annotation/xsd:appinfo/tcm:ExtensionXml/mapping:property", nsmgr);
            if (propertyNode != null)
            {
                property += "," + propertyNode.InnerText;
            }
            fieldSemantics.Append(BuildFieldSemanticsJson(property, fieldTypeOf));

            // handle embedded fields
            StringBuilder embeddedFields = new StringBuilder();
            XmlNode embeddedSchemaNode = fieldNode.SelectSingleNode("xsd:annotation/xsd:appinfo/tcm:EmbeddedSchema", nsmgr);
            if (embeddedSchemaNode != null)
            {
                string uri = embeddedSchemaNode.Attributes["href", "http://www.w3.org/1999/xlink"].Value;
                Schema embeddedSchema = (Schema)MEngine.GetObject(uri);
                string embeddedTypeOf = string.Format("{0}:{1}", DefaultVocabularyPrefix, embeddedSchema.RootElementName);

                // append schema typeof from appdata for embedded schemas
                ApplicationData appData = embeddedSchema.LoadApplicationData(TypeOfAppDataId);
                if (appData != null)
                {
                    embeddedTypeOf += "," + Encoding.Unicode.GetString(appData.Data);
                }

                embeddedFields.Append(BuildSchemaFieldsJson(embeddedSchema, path, embeddedTypeOf, nsmgr, true));
            }

            // TODO: handle link fields
            //XmlAttribute typeAttribute = fieldNode.Attributes["type"];
            //if (typeAttribute != null)
            //{
            //    bool isSimpleLink = typeAttribute.Value.Equals("tcmi:SimpleLink");
            //    XmlNode allowedTargetSchemasNode = fieldNode.SelectSingleNode("xsd:annotation/xsd:appinfo/tcm:AllowedTargetSchemas", nsmgr);
            //    if (allowedTargetSchemasNode != null)
            //    {
            //        foreach (XmlNode allowedTargetSchemaNode in allowedTargetSchemasNode.SelectNodes("tcm:TargetSchema", nsmgr))
            //        {
            //            string uri = allowedTargetSchemaNode.Attributes["href", "http://www.w3.org/1999/xlink"].Value;
            //            Schema allowedTargetSchema = (Schema)MEngine.GetObject(uri);
            //
            //        }
            //    }
            //    else
            //    {
            //        // if there are no allowed target schemas, all schemas are allowed...
            //    }
            //}
            //else
            //{
            //    // if there is no type attribute, it is not a simple type so look for <xsd:complexType> inside the element
            //}

            return string.Format("{{\"Name\":{0},\"Path\":{1},\"IsMultiValue\":{2},\"Semantics\":[{3}],\"Fields\":[{4}]}}", Json.Encode(name), Json.Encode(path), Json.Encode(isMultiValue), fieldSemantics, embeddedFields);
        }

        // schema semantics: {"Prefix":"s","Entity":"Article"}
        private static string BuildSchemaSemanticsJson(string input)
        {
            StringBuilder semantics = new StringBuilder();
            if (!string.IsNullOrEmpty(input))
            {
                // input = "s:Article" but can also be "s:Article,x:Something"
                string[] values = input.Split(',');
                bool first = true;
                foreach (var value in values)
                {
                    if (first)
                    {
                        first = false;
                    }
                    else
                    {
                        semantics.Append(",");
                    }
                    string[] parts = value.Split(':');
                    semantics.AppendFormat("{{\"Prefix\":{0},\"Entity\":{1}}}", Json.Encode(parts[0]), Json.Encode(parts[1]));
                }
            }

            return semantics.ToString();
        }

        // field semantics: {"Prefix":"s","Entity":"Article","Property":"headline"}
        private static string BuildFieldSemanticsJson(string input, string entity)
        {
            Dictionary<string, string> entities = new Dictionary<string, string>();
            StringBuilder semantics = new StringBuilder();
            if (!string.IsNullOrEmpty(input))
            {
                // entity = "s:Article" but can also be "s:Article,x:Something"
                string[] values = entity.Split(',');
                foreach (var value in values)
                {
                    string[] parts = value.Split(':');
                    entities.Add(parts[0], parts[1]);
                }

                // input = "s:headline" but can also be "s:headline,x:something"
                string[] properties = input.Split(',');
                for (int index = 0; index < properties.Length; index++)
                {
                    if (index > 0)
                    {
                        semantics.Append(",");
                    }
                    var value = properties[index];
                    string[] parts = value.Split(':');
                    semantics.AppendFormat("{{\"Prefix\":{0},\"Entity\":{1},\"Property\":{2}}}", Json.Encode(parts[0]), Json.Encode(entities[parts[0]]), Json.Encode(parts[1]));
                }
            }

            return semantics.ToString();
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
                    var module = GetModuleName(schema.WebDavUrl);
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
                    var module = GetModuleName(template.WebDavUrl);
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

        private static string GetRegionFromTemplate(ComponentTemplate template)
        {
            string region = "Main";

            string title = template.Title;
            if (title.Contains('['))
            {
                int start = title.IndexOf('[') + 1;
                int length = title.IndexOf(']') - start;
                region = title.Substring(start, length);
            }
            return region;
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

        private string GetModuleName(string fullItemWebdavUrl)
        {
            //The module name is the name of the folder within the first level of the module root folder 
            //in which the item lives
            if (fullItemWebdavUrl.StartsWith(_moduleRoot))
            {
                Logger.Debug(fullItemWebdavUrl + ":" + _moduleRoot);
                var res = fullItemWebdavUrl.Substring(_moduleRoot.Length + 1);
                var pos = res.IndexOf("/", StringComparison.Ordinal);
                Logger.Debug(res);
                return res.Substring(0, pos).ToLower();
            }
            return null;
        }
    }
}

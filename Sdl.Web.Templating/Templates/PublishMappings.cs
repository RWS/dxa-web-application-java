using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;
using System.Xml;
using System.Xml.Linq;
using Sdl.Web.Tridion.Common;
using Tridion;
using Tridion.ContentManager;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;
using Tridion.ContentManager.ContentManagement.Fields;

namespace Sdl.Web.Tridion.Templates
{
    /// <summary>
    /// Publishes schema and region mapping information in JSON format
    /// </summary>
    [TcmTemplateTitle("Publish Mappings")]
    public class PublishMappings : TemplateBase
    {
        private const string SchemasConfigName = "schemas";
        //private const string MappingsConfigName = "mappings";
        private const string RegionConfigName = "regions";
        private const string VocabulariesConfigName = "vocabularies";
        private const string VocabulariesAppDataId = "http://www.sdl.com/tridion/SemanticMapping/vocabularies";
        private const string TypeOfAppDataId = "http://www.sdl.com/tridion/SemanticMapping/typeof";
        private const string TypeOfAppDataStartElement = "<typeof>";
        private const string TypeOfAppDataEndElement = "</typeof>";
        private const string DefaultVocabularyPrefix = "tri";
        private const string DefaultVocabulary = "http://www.sdl.com/web/schemas/core";
        
        // list of known namespaces that are used in our schemas
        private readonly Dictionary<string, string> _namespaces = new Dictionary<string, string>
            {
                {Constants.XsdPrefix, Constants.XsdNamespace},
                {Constants.TcmPrefix, Constants.TcmNamespace},
                {Constants.XlinkPrefix, Constants.XlinkNamespace},
                {Constants.XhtmlPrefix, Constants.XhtmlNamespace},
                {"tcmi","http://www.tridion.com/ContentManager/5.0/Instance"},
                {"mapping", "http://www.sdl.com/tridion/SemanticMapping"}
            };

        //private string _moduleRoot;

        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);
            //The core configuration component should be the one being processed by the template
            var coreConfigComponent = GetComponent();
            var sg = GetSystemStructureGroup("mappings");
            //_moduleRoot = GetModulesRoot(coreConfigComponent);

            //Get all the active modules
            //Dictionary<string, Component> moduleComponents = GetActiveModules(coreConfigComponent);
            List<string> filesCreated = new List<string>();
            if (IsMasterWebPublication())
            {
                filesCreated.AddRange(PublishJsonData(ReadMappingsData(), coreConfigComponent, "mapping", sg, true));
                filesCreated.Add(PublishJsonData(ReadPageTemplateIncludes(), coreConfigComponent, "includes", "includes", sg));
            }
            //Publish the boostrap list, this is used by the web application to load in all other mapping files
            PublishBootstrapJson(filesCreated, coreConfigComponent, sg, "mapping-");
        }

        private Dictionary<string, List<string>> ReadMappingsData()
        {
            bool containsDefaultVocabulary = false;

            // generate a list of vocabulary prefix and name from appdata
            var res = new Dictionary<string, List<string>> { { VocabulariesConfigName, new List<string>() } };
            ApplicationData globalAppData = Engine.GetSession().SystemManager.LoadGlobalApplicationData(VocabulariesAppDataId);
            if (globalAppData != null)
            {
                XElement vocabulariesXml = XElement.Parse(Encoding.Unicode.GetString(globalAppData.Data));
                foreach (var vocabulary in vocabulariesXml.Elements())
                {
                    string prefix = vocabulary.Attribute("prefix").Value;
                    res[VocabulariesConfigName].Add(String.Format("{{\"Prefix\":{0},\"Vocab\":{1}}}", JsonEncode(prefix), JsonEncode(vocabulary.Attribute("name").Value)));
                    if (prefix.Equals(DefaultVocabularyPrefix))
                    {
                        containsDefaultVocabulary = true;
                    }
                }
            }
            // add default vocabulary if it is not there already
            if (!containsDefaultVocabulary)
            {
                res[VocabulariesConfigName].Add(String.Format("{{\"Prefix\":{0},\"Vocab\":{1}}}", JsonEncode(DefaultVocabularyPrefix), JsonEncode(DefaultVocabulary)));
            }

            // generate a list of schema + id, separated by module
            var schemaFilter = new RepositoryItemsFilter(Engine.GetSession())
            {
                Recursive = true,
                ItemTypes = new List<ItemType> { ItemType.Schema },
                BaseColumns = ListBaseColumns.Extended
            };
            res.Add(SchemasConfigName, new List<string>());
            foreach (XmlElement item in GetPublication().GetListItems(schemaFilter).ChildNodes)
            {
                var type = item.GetAttribute("Type");
                var subType = item.GetAttribute("SubType");
                // we consider normal schemas (type=8 subtype=0) and multimedia schemas (type=8 subtype=1)
                if ((type == "8" && (subType == "0" || subType == "1")))
                {
                    var id = item.GetAttribute("ID");
                    var schema = (Schema)Engine.GetObject(id);
                    
                    // multimedia schemas don't have a root element name, so lets use its title without any invalid characters
                    string rootElementName = schema.RootElementName;
                    if (String.IsNullOrEmpty(rootElementName))
                    {
                        rootElementName = Regex.Replace(schema.Title.Trim(), @"[^A-Za-z0-9.]+", "");
                    }
                    // add schema typeof using tridion standard implementation vocabulary prefix
                    string typeOf = String.Format("{0}:{1}", DefaultVocabularyPrefix, rootElementName);
                    StringBuilder schemaSemantics = new StringBuilder();
                    // append schema typeof from appdata 
                    ApplicationData appData = schema.LoadApplicationData(TypeOfAppDataId);
                    if (appData != null)
                    {
                        typeOf += "," + ExtractTypeOfAppData(appData);
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

                    res[SchemasConfigName].Add(String.Format("{{\"Id\":{0},\"RootElement\":{1},\"Fields\":[{2}],\"Semantics\":[{3}]}}", JsonEncode(schema.Id.ItemId), JsonEncode(rootElementName), fields, schemaSemantics));
                    
                }
            }

            // get region mappings for all templates
            var regions = BuildRegionMappings();
            res.Add(RegionConfigName, new List<string>());

            foreach (var region in regions)
            {
                StringBuilder allowedComponentTypes = new StringBuilder();
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
                res[RegionConfigName].Add(String.Format("{{\"Region\":{0},\"ComponentTypes\":[{1}]}}", JsonEncode(region.Key), allowedComponentTypes));
            }

            return res;
        }

        private Dictionary<string, List<string>> BuildRegionMappings()
        {
            // format:  region { schema, template } 
            Dictionary<string, List<string>> regions = new Dictionary<string, List<string>>();

            var templateFilter = new ComponentTemplatesFilter(Engine.GetSession()) { BaseColumns = ListBaseColumns.Extended };
            foreach (XmlElement item in GetPublication().GetListComponentTemplates(templateFilter).ChildNodes)
            {
                var id = item.GetAttribute("ID");
                var template = (ComponentTemplate)Engine.GetObject(id);
                var region = GetRegionFromTemplate(template);
                
                if (!regions.ContainsKey(region))
                {
                    regions.Add(region, new List<string>());
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
                    allowedComponentTypes.AppendFormat("{{\"Schema\":{0},\"Template\":{1}}}", JsonEncode(schema.Id.GetVersionlessUri().ToString()), JsonEncode(template.Id.GetVersionlessUri().ToString()));
                }

                // do not append empty strings (template.RelatedSchemas can be empty)
                if (allowedComponentTypes.Length > 0)
                {
                    regions[region].Add(allowedComponentTypes.ToString());
                }
            }
            return regions;
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
            string xpath = String.Format("/xsd:schema/xsd:element[@name='{0}']/xsd:complexType/xsd:sequence/xsd:element", schema.RootElementName);
            if (embedded)
            {
                xpath = String.Format("/xsd:schema/xsd:complexType[@name='{0}']/xsd:sequence/xsd:element", schema.RootElementName);
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
            string property = String.Format("{0}:{1}", DefaultVocabularyPrefix, fieldNode.Attributes["name"].Value);

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
                Schema embeddedSchema = (Schema)Engine.GetObject(uri);
                string embeddedTypeOf = String.Format("{0}:{1}", DefaultVocabularyPrefix, embeddedSchema.RootElementName);

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

            return String.Format("{{\"Name\":{0},\"Path\":{1},\"IsMultiValue\":{2},\"Semantics\":[{3}],\"Fields\":[{4}]}}", JsonEncode(name), JsonEncode(path), JsonEncode(isMultiValue), fieldSemantics, embeddedFields);
        }

        // schema semantics: {"Prefix":"s","Entity":"Article"}
        private string BuildSchemaSemanticsJson(string input)
        {
            StringBuilder semantics = new StringBuilder();
            if (!String.IsNullOrEmpty(input))
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
                    semantics.AppendFormat("{{\"Prefix\":{0},\"Entity\":{1}}}", JsonEncode(parts[0]), JsonEncode(parts[1]));
                }
            }

            return semantics.ToString();
        }

        // field semantics: {"Prefix":"s","Entity":"Article","Property":"headline"}
        private string BuildFieldSemanticsJson(string input, string entity)
        {
            Dictionary<string, string> entities = new Dictionary<string, string>();
            StringBuilder semantics = new StringBuilder();
            bool first = true;
            if (!String.IsNullOrEmpty(input))
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
                    var value = properties[index];
                    string[] parts = value.Split(':');
                    if (entities.ContainsKey(parts[0]))
                    {
                        if (!first)
                        {
                            semantics.Append(",");
                        }
                        first = false;
                        semantics.AppendFormat("{{\"Prefix\":{0},\"Entity\":{1},\"Property\":{2}}}", JsonEncode(parts[0]), JsonEncode(entities[parts[0]]), JsonEncode(parts[1]));
                    }
                }
            }

            return semantics.ToString();
        }


        protected virtual List<string> ReadPageTemplateIncludes()
        {
            //Generate a list of Page Templates which have includes in the metadata
            var res = new List<string>();
            var templateFilter = new RepositoryItemsFilter(Engine.GetSession());
            templateFilter.ItemTypes = new List<ItemType> { ItemType.PageTemplate };
            templateFilter.Recursive = true;
            foreach (XmlElement item in GetPublication().GetListItems(templateFilter).ChildNodes)
            {
                var id = item.GetAttribute("ID");
                var template = (PageTemplate)Engine.GetObject(id);
                if (template.MetadataSchema != null && template.Metadata != null)
                {
                    ItemFields meta = new ItemFields(template.Metadata, template.MetadataSchema);
                    var values = meta.GetTextValues("includes");
                    if (values != null)
                    {
                        var includes = new List<string>();
                        foreach (var val in values)
                        {
                            includes.Add(JsonEncode(val));
                        }
                        var json = String.Format("\"{0}\":[{1}]", template.Id.ItemId, String.Join(",\n", includes));
                        res.Add(json);
                    }
                }
            }
            return res;
        }


        private static string ExtractTypeOfAppData(ApplicationData appData)
        {
            if (appData != null)
            {
                // appdata is supposed to be a unicode encoded string
                string xmlData = Encoding.Unicode.GetString(appData.Data);

                // remove start and end xml element from appdata string
                return xmlData.Replace(TypeOfAppDataStartElement, String.Empty).Replace(TypeOfAppDataEndElement, String.Empty);
            }
            return null;
        }
    }
}

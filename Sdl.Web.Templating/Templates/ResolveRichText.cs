using System.Text;
using Sdl.Web.Tridion.Common;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Xml;
using Tridion;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;

namespace Sdl.Web.Tridion.Templates
{
    /// <summary>
    /// Resolves rich text fields from the package output.
    /// Replacing img tags that point to a YouTube video with a youtube tag, 
    /// so these can be resolved correctly by the web application.
    /// </summary>
    [TcmTemplateTitle("Resolve Rich Text")]
    [TcmTemplateParameterSchema("resource:Sdl.Web.Tridion.Resources.ResolveRichTextParameters.xsd")]
    public class ResolveRichText : TemplateBase
    {
        private const string SchemaUriAttribute = "data-schemaUri";
        private const string FileNameAttribute = "data-multimediaFileName";
        private const string MimeTypeAttribute = "data-multimediaMimeType";
        private const string LinkPattern = @"xlink:href=\\""(tcm\:\d+\-\d+)\\""";
        private const string XhtmlPattern = " xmlns=\\\"http://www.w3.org/1999/xhtml\\\"";

        private List<string> _metaFieldNames = new List<string>();
            
        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);
            Component comp = GetComponent();
            if (IsPageTemplate() || comp == null)
            {
                Logger.Error("No Component found (is this a Page Template?)");
                return;
            }
            Item outputItem = package.GetByName(Package.OutputName);
            if (outputItem == null)
            {
                Logger.Error("No Output package item found (is this TBB placed at the end?)");
                return;
            }
            _metaFieldNames = (package.GetValue("multimediaLinkAttributes") ?? String.Empty).Split(',').Select(s => s.Trim()).ToList();

            // resolve rich text fields
            string output = outputItem.GetAsString();
            package.Remove(outputItem); 
            if (output.StartsWith("<"))
            {
                Logger.Debug("Content is XML");
                //XML - only for backwards compatibility
                package.PushItem(Package.OutputName, package.CreateXmlDocumentItem(ContentType.Xml, ResolveXmlContent(output)));
            }
            else
            {
                Logger.Debug("Content is JSON");
                //JSON
                package.PushItem(Package.OutputName, package.CreateStringItem(ContentType.Text, ResolveJsonContent(output)));
            }
        }

        private string ResolveJsonContent(string content)
        {
            //remove XHTML namespace
            content = content.Replace(XhtmlPattern, String.Empty);

            //add data attributes to component links
            content = Regex.Replace(content, LinkPattern, delegate(Match match)
            {
                Logger.Debug("Found RTF link match: " + match.Value);
                string compId = match.Groups[1].Value;
                string replaced = match.Value;
                Component comp = (Component)Engine.GetObject(compId);

                // add semantic schema attribute for model mapping
                if (comp != null)
                {
                    // set base attributes for multimedia component
                    string attributes = String.Empty;
                    StringBuilder attributesBuilder = new StringBuilder();
                    attributesBuilder.AppendFormat(" {0}=\"{1}\"", SchemaUriAttribute, comp.Schema.Id);
                    attributesBuilder.AppendFormat(" {0}=\"{1}\"", FileNameAttribute, comp.BinaryContent.Filename);
                    attributesBuilder.AppendFormat(" {0}=\"{1}\"", MimeTypeAttribute, comp.BinaryContent.MultimediaType.MimeType);

                    // resolve metadata into additional data-attributes
                    if (comp.Metadata != null)
                    {
                        ItemFields fields = new ItemFields(comp.Metadata, comp.MetadataSchema);
                        attributesBuilder.Append(ProcessFields(fields));
                    }

                    // encode and strip first and last character (quotes added by encode)
                    if (attributesBuilder.Length > 0)
                    {
                        attributes = JsonEncode(attributesBuilder.ToString()).Substring(1);
                        attributes = attributes.Substring(0, attributes.Length - 1);
                    }
                    replaced = replaced + attributes;
                }
                return replaced;
            });
            return content;
        }

        private string ProcessFields(ItemFields fields)
        {
            StringBuilder attributesBuilder = new StringBuilder(); 
            if (fields!=null)
            {
                Logger.Debug(String.Join(", ", _metaFieldNames));
                foreach (string fieldname in _metaFieldNames)
                {
                    Logger.Debug("Processing field: " + fieldname);
                    if (fields.Contains(fieldname))
                    {
                        string attribute = String.Format(" data-{0}=\"{1}\"", fieldname, System.Net.WebUtility.HtmlEncode(fields.GetSingleFieldValue(fieldname)));
                        Logger.Debug("Attribute:" + attribute);
                        // TODO: XML encode the value
                        attributesBuilder.Append(attribute);
                    }
                }

                foreach (ItemField field in fields)
                {
                    if (field is EmbeddedSchemaField)
                    {
                        attributesBuilder.Append(ProcessFields(((EmbeddedSchemaField)field).Value));
                    }
                }
            }
            Logger.Debug("attributes:" + attributesBuilder);
            return attributesBuilder.ToString();
        }

        private XmlDocument ResolveXmlContent(string content)
        {
            XmlDocument doc = new XmlDocument();
            doc.LoadXml(content);
            XmlNodeList fields = doc.SelectNodes("//Field[@FieldType='Xhtml']/Values/string");
            foreach (XmlElement field in fields)
            {
                field.InnerXml = ResolveXhtml(field.InnerXml);
            }
            return doc;
        }

        private string ResolveXhtml(string input)
        {
            XmlDocument xhtml = new XmlDocument();
            XmlNamespaceManager nsmgr = new XmlNamespaceManager(xhtml.NameTable);
            nsmgr.AddNamespace(Constants.XlinkPrefix, Constants.XlinkNamespace);
            xhtml.LoadXml(String.Format("<root>{0}</root>", UnEscape(input)));

            // locate linked components
            foreach (XmlElement link in xhtml.SelectNodes("//*[@xlink:href[starts-with(string(.),'tcm:')]]", nsmgr))
            {
                string uri = link.Attributes["xlink:href"].IfNotNull(attr => attr.Value);
                //string title = img.Attributes["xlink:title"].IfNotNull(attr => attr.Value);
                //string src = img.Attributes["src"].IfNotNull(attr => attr.Value);
                if (!string.IsNullOrEmpty(uri))
                {
                    Component comp = (Component)Engine.GetObject(uri);

                    // resolve multimedia component
                    if (comp != null)
                    {
                        // set base attributes for multimedia component
                        link.SetAttribute(SchemaUriAttribute, comp.Schema.Id);
                        link.SetAttribute(FileNameAttribute, comp.BinaryContent.Filename);
                        link.SetAttribute(MimeTypeAttribute, comp.BinaryContent.MultimediaType.MimeType);

                        // resolve metadata into additional data-attributes
                        if (comp.Metadata != null)
                        {
                            ItemFields fields = new ItemFields(comp.Metadata, comp.MetadataSchema);
                            ProcessFields(fields, link);
                        }
                    }
                }
            }
            return Escape(xhtml.DocumentElement.InnerXml);
        }

        private void ProcessFields(ItemFields fields, XmlElement link)
        {
            if (fields != null)
            {
                foreach (string fieldname in _metaFieldNames)
                {
                    if (fields.Contains(fieldname))
                    {
                        link.SetAttribute("data-" + fieldname, fields.GetSingleFieldValue(fieldname));
                    }
                }

                foreach (ItemField field in fields)
                {
                    if (field is EmbeddedSchemaField)
                    {
                        ProcessFields(((EmbeddedSchemaField)field).Value, link);
                    }
                }
            }
        }

        private static string UnEscape(string input)
        {
            return input.Replace("&lt;", "<").Replace("&gt;", ">");
        }

        private static string Escape(string input)
        {
            // escape angle brackets and remove xhtml namespace
            string xmlns = String.Format(" {0}=\"{1}\"", Constants.XhtmlPrefix, Constants.XhtmlNamespace);
            return input.Replace("<", "&lt;").Replace(">", "&gt;").Replace(xmlns, String.Empty);
        }
    }
}

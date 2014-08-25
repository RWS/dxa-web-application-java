using System;
using System.Xml;
using System.Linq;
using Sdl.Web.Tridion.Common;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;
using System.Collections.Generic;

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
        private const string XhtmlNamespace = "http://www.w3.org/1999/xhtml";
        private const string XlinkNamespace = "http://www.w3.org/1999/xlink";
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
            XmlDocument doc = new XmlDocument();
            string output = outputItem.GetAsString();
            doc.LoadXml(output);
            var fields = doc.SelectNodes("//Field[@FieldType='Xhtml']/Values/string");
            foreach (XmlElement field in fields)
            {
                field.InnerXml = ResolveXhtml(field.InnerXml);
            }
            package.Remove(outputItem);
            package.PushItem(Package.OutputName, package.CreateXmlDocumentItem(ContentType.Xml, doc)); 
        }

        private string ResolveXhtml(string input)
        {
            XmlDocument xhtml = new XmlDocument();
            var nsmgr = new XmlNamespaceManager(xhtml.NameTable);
            nsmgr.AddNamespace("xlink", XlinkNamespace);
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
                    // resolve youtube video
                    if (comp != null)
                    {
                        ItemFields fields = new ItemFields(comp.Metadata, comp.MetadataSchema);
                        ProcessFields(fields, link);
                    }
                }
            }
            return Escape(xhtml.DocumentElement.InnerXml);
        }

        private void ProcessFields(ItemFields fields, XmlElement link)
        {
            foreach(var fieldname in _metaFieldNames)
            {
                if (fields.Contains(fieldname))
                {
                    link.SetAttribute("data-" + fieldname, fields.GetSingleFieldValue(fieldname));
                }
            }
            foreach(var field in fields)
            {
                if (field is EmbeddedSchemaField)
                {
                    ProcessFields(((EmbeddedSchemaField)field).Value, link);
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
            string xmlns = String.Format(" xmlns=\"{0}\"", XhtmlNamespace);
            return input.Replace("<", "&lt;").Replace(">", "&gt;").Replace(xmlns, String.Empty);
        }
    }
}

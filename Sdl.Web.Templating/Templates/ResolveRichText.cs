using System;
using System.Xml;
using Sdl.Web.Tridion.Common;
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
    public class ResolveRichText : TemplateBase
    {
        private const string XhtmlNamespace = "http://www.w3.org/1999/xhtml";
        private const string XlinkNamespace = "http://www.w3.org/1999/xlink";
        private const string YouTubeVideoElement = "<xhtml:youtube xlink:href=\"{0}\" xlink:title=\"{1}\" src=\"{2}\" id=\"{3}\" headline=\"{4}\" xmlns:xhtml=\"{5}\" xmlns:xlink=\"{6}\"></xhtml:youtube>";

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
            bool updated = false;

            // resolve rich text fields
            XmlDocument doc = new XmlDocument();
            string output = outputItem.GetAsString();
            doc.LoadXml(output);
            var fields = doc.SelectNodes("//Field[@FieldType='Xhtml']/Values/string");
            foreach (XmlElement field in fields)
            {
                string resolved = ResolveXhtml(field.InnerXml, output);
                updated = !output.Equals(resolved);
                if (updated)
                {
                    output = resolved;                    
                }
            }

            if (updated)
            {
                package.Remove(outputItem);
                package.PushItem(Package.OutputName, package.CreateStringItem(ContentType.Xml, output));                
            }
        }

        private string ResolveXhtml(string input, string output)
        {
            XmlDocument xhtml = new XmlDocument();
            var nsmgr = new XmlNamespaceManager(xhtml.NameTable);
            nsmgr.AddNamespace("xhtml", XhtmlNamespace);
            nsmgr.AddNamespace("xlink", XlinkNamespace);
            xhtml.LoadXml(String.Format("<root>{0}</root>", UnEscape(input)));

            // locate possible youtube videos by searching for img tags with a tcmuri
            foreach (XmlElement img in xhtml.SelectNodes("//xhtml:img[@xlink:href[starts-with(string(.),'tcm:')]]", nsmgr))
            {
                string uri = img.Attributes["xlink:href"].IfNotNull(attr => attr.Value);
                string title = img.Attributes["xlink:title"].IfNotNull(attr => attr.Value);
                string src = img.Attributes["src"].IfNotNull(attr => attr.Value);
                if (!string.IsNullOrEmpty(uri))
                {
                    Component comp = (Component)Engine.GetObject(uri);
                    // resolve youtube video
                    if (comp != null && comp.Schema.Title.ToLower().Contains("youtube"))
                    {
                        ItemFields fields = new ItemFields(comp.Metadata, comp.MetadataSchema);
                        if (fields.Contains("youTubeId"))
                        {
                            string id = fields.GetTextValue("youTubeId");
                            string headline = fields.GetTextValue("headline");
                            string image = Escape(img.OuterXml);
                            string video = Escape(String.Format(YouTubeVideoElement, uri, title, src, id, headline, XhtmlNamespace, XlinkNamespace));
                            // replace image with youtube video in output
                            output = output.Replace(image, video);
                            Logger.Info(String.Format("Resolved img {0} to youtube video {1}", uri, id));
                        }
                    }
                }
            }

            return output;
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

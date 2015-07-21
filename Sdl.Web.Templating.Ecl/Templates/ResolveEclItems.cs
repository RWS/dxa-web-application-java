using System;
using System.Xml;
using Sdl.Web.Tridion.Common;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;
using Tridion.ExternalContentLibrary.Templating.V2;
using Newtonsoft.Json;

namespace Sdl.Web.Tridion.Templates
{
    /// <summary>
    /// Resolves ECL items from the package output.
    /// Update URL tags with the External URL if available so these can be used directly by the web application. 
    /// </summary>
    /// <remarks>
    /// Should be placed after the "Publish binaries for component" TBB
    /// </remarks>
    [TcmTemplateTitle("Resolve External Content Library Items")]
    [TcmTemplateParameterSchema("resource:Sdl.Web.Tridion.Resources.ResolveEclItemsParameters.xsd")]
    public class ResolveEclItems : TemplateBase
    {
        private ExternalContentLibraryFunctionSource _eclFunctions;

        protected void Init(Engine engine, Package package)
        {
            Initialize(engine, package);
            _eclFunctions = new ExternalContentLibraryFunctionSource();
            _eclFunctions.Initialize(engine, package);
        }

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
            string output = outputItem.GetAsString();
            if (outputItem.ContentType.Equals(ContentType.Xml))
            {
                Logger.Debug("Content is XML");
                //XML - only for backwards compatibility
                XmlDocument xmlDocument;
                if (ResolveXmlContent(output, out xmlDocument))
                {
                    package.Remove(outputItem);
                    package.PushItem(Package.OutputName, package.CreateXmlDocumentItem(ContentType.Xml, xmlDocument));
                }
            }
            else
            {
                Logger.Debug("Content is JSON");
                //JSON
                string json;
                if (ResolveJsonContent(output, out json))
                {
                    package.Remove(outputItem);
                    package.PushItem(Package.OutputName, package.CreateStringItem(ContentType.Text, json));
                }
            }
        }

        private bool ResolveJsonContent(string content, out string json)
        {
            json = content;

            XmlDocument xmlDocument = JsonConvert.DeserializeXmlNode(json, "Component", true);
            bool containsEclReferences = ResolveContentDocument(xmlDocument);
            if (containsEclReferences)
            {
                json = JsonConvert.SerializeXmlNode(xmlDocument, Newtonsoft.Json.Formatting.None, true);
            }

            return containsEclReferences;
        }

        private bool ResolveXmlContent(string content, out XmlDocument xmlDocument)
        {
            xmlDocument = new XmlDocument();
            xmlDocument.LoadXml(content);

            return ResolveContentDocument(xmlDocument);
        }

        private bool ResolveContentDocument(XmlDocument xmlDocument)
        {
            bool containsEclReferences = false;

            XmlNodeList multimediaComponentElements = xmlDocument.SelectNodes("//Multimedia[MimeType='application/externalcontentlibrary']/..");

            Logger.Debug(String.Format("Resolving {0} External Content Library reference(s)", multimediaComponentElements.Count));

            foreach (XmlElement multimediaComponentElement in multimediaComponentElements)
            {
                ResolveEclReference(multimediaComponentElement);
                containsEclReferences = true;
            }

            return containsEclReferences;
        }

        private void ResolveEclReference(XmlElement multimediaComponentElement)
        {
            Logger.Debug(String.Format("Multimedia Component XML [{0}]", multimediaComponentElement.OuterXml));

            string id = multimediaComponentElement.SelectSingleNode("Id").IfNotNull(i => i.InnerText);
            if (!String.IsNullOrEmpty(id))
            {
                XmlNode urlNode = multimediaComponentElement.SelectSingleNode("Multimedia/Url");
                if (urlNode != null)
                {
                    if (_eclFunctions.IsExternalContentLibraryComponent(id))
                    {
                        // TODO: should we consider using the template fragment somehow?
                        // TODO: direct link could be null, then content is available and we should add a binary to the package and publish that
                        // in case of the latter, shouldn't this be handled in the DD4T Publish binaries for component TBB? 
                        urlNode.InnerText = _eclFunctions.GetExternalContentLibraryDirectLink(id);
                    }
                }
            }
        }
    }
}

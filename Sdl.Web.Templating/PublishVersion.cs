using Sdl.Web.ContentManagement.Templating;
using System;
using System.Collections.Generic;
using System.Dynamic;
using System.IO;
using System.Linq;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Threading.Tasks;
using Tridion.ContentManager;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.Templating;
using Sdl.Web.ContentManagement.ExtensionMethods;
using Tridion.ContentManager.ContentManagement.Fields;
using System.Web.Helpers;
using Tridion.ContentManager.Templating.Assembly;

namespace Sdl.Web.ContentManagement.Templating
{
    /// <summary>
    /// Publish all files required to make a version release for the implementation:
    /// * Configuration
    /// * Resources
    /// * Design Elements (if applicable)
    /// * Views (if applicable)
    /// </summary>
    [TcmTemplateTitle("Publish Version")]
    public class PublishVersion : TemplateBase
    {
        private const string JSON_MIMETYPE = "application/json";
        private const string JSON_EXTENSION = ".json";
        private const string BOOTSTRAP_FILENAME = "_all" + JSON_EXTENSION;

        public override void Transform(Engine engine, Package package)
        {
            this.Initialize(engine, package);
            var folderWebDavUrl = this.GetPublication().RootFolder.WebDavUrl + "/" + package.GetValue("folderWebDavUrl");
            var sgUri = m_Package.GetValue("sg_TargetStructureGroup");
            var sg = (StructureGroup)m_Engine.GetObject(engine.LocalizeUri(new TcmUri(sgUri)));
            Folder folder = null;
            try
            {
                folder = (Folder)m_Engine.GetObject(folderWebDavUrl);
            }
            catch
            {
                throw new Exception(String.Format("Cannot find modules folder: {0}. Please check it exists and update your TBB parameters accordingly.",folderWebDavUrl));
            }
            if (sg == null)
            {
                throw new Exception(String.Format("Could not find structure group {0} in context publication {1}", sgUri, this.GetPublication().Title));
            }

            List<string> bootstrapFiles = new List<string>();

            bootstrapFiles.Add(Json.Encode(PublishAssets(folder, sg, "config")));
            bootstrapFiles.Add(Json.Encode(PublishAssets(folder, sg, "resources")));
            //TODO we need to dynamically create this bootstrap output
            Item jsonBootstrap = m_Package.CreateStringItem(ContentType.Text, String.Format("{{\"files\":[{0}]}}", String.Join(",", bootstrapFiles)));
            m_Package.PushItem("Output", jsonBootstrap);
            m_Engine.PublishingContext.RenderedItem.AddBinary(jsonBootstrap.GetAsStream(), BOOTSTRAP_FILENAME, sg, "bootstrap", this.GetComponent(), JSON_MIMETYPE);
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
            foreach (var item in GetOrganizationalItemContents(folder, ItemType.Component, false))
            {
                Component comp = (Component)m_Engine.GetObject(item.Key.ToString());
                var fields = new ItemFields(comp.Content, comp.Schema);
                var filename = comp.Title.ToLower() + JSON_EXTENSION;
                if (filename == BOOTSTRAP_FILENAME)
                {
                    //The _All component is a dummy component used to publish a bootstrap file listing only
                    allComponent = comp;
                    continue;
                }
                List<string> settings = new List<string>();
                //TODO maybe this would be nicer with some kind of JSON serializer/writer...
                var configFields = fields.GetEmbeddedFields("settings");
                if (configFields.Count() > 0)
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
                Item jsonItem = m_Package.CreateStringItem(ContentType.Text, String.Format("{{{0}}}", String.Join(",\n", settings)));
                m_Package.PushItem(filename, jsonItem);
                var binary = m_Engine.PublishingContext.RenderedItem.AddBinary(jsonItem.GetAsStream(), filename, sg, type, comp, JSON_MIMETYPE);
                files.Add(Json.Encode(binary.Url));
            }
            if (allComponent == null)
            {
                throw new Exception(String.Format("Cannot find '_All' component. Please ensure that this component is in the {0} folder.", folder.WebDavUrl));
            }
            Item filesItem = m_Package.CreateStringItem(ContentType.Text, String.Format("{{\"files\":[{0}]}}", String.Join(",", files)));
            //We create a special asset file _all.js which contains the path of all other asset files of this type that have been published
            var bootstrapBinary = m_Engine.PublishingContext.RenderedItem.AddBinary(filesItem.GetAsStream(), BOOTSTRAP_FILENAME, sg, type, allComponent, JSON_MIMETYPE);
            return bootstrapBinary.Url;
        }
    }

}

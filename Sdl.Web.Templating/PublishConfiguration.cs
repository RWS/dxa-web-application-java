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
    [TcmTemplateTitle("Publish Configuration")]
    public class PublishConfiguration : TemplateBase
    {
        public override void Transform(Engine engine, Package package)
        {
            this.Initialize(engine, package);
            var folderWebDavUrl = this.GetPublication().RootFolder.WebDavUrl + "/" + package.GetValue("folderWebDavUrl");
            var sg = (StructureGroup)m_Engine.GetObject(engine.LocalizeUri(new TcmUri(m_Package.GetValue("sg_TargetStructureGroup"))));
            Folder folder = null;
            try
            {
                folder = (Folder)m_Engine.GetObject(folderWebDavUrl);
            }
            catch
            {
                throw new Exception(String.Format("Cannot find folder: {0}. Please check it exists and update your TBB parameters accordingly.",folderWebDavUrl));
            }
            PublishConfigFromFolder(folder, sg);           
        }

        private void PublishConfigFromFolder(Folder folder, StructureGroup sg)
        {
            //TODO maybe this would be nicer with some kind of JSON serializer/writer...
            string variant = "config";
            string mimetype = "application/json";
            List<string> files = new List<string>();
            Component allComponent = null;
            foreach (var item in GetOrganizationalItemContents(folder, ItemType.Component, false))
            {
                Component comp = (Component)m_Engine.GetObject(item.Key.ToString());
                var fields = new ItemFields(comp.Content, comp.Schema);
                var filename = comp.Title.ToLower() + ".js";
                if (filename=="_all.js")
                {
                    //The _All component is a dummy component used to publish a list of config files only
                    allComponent = comp;
                    continue;
                }
                List<string> settings = new List<string>();
                foreach(var setting in fields.GetEmbeddedFields("settings"))
                {
                    settings.Add(String.Format("{0}:{1}", Json.Encode(setting.GetTextValue("name")), Json.Encode(setting.GetTextValue("value"))));
                }
                Item jsonItem = m_Package.CreateStringItem(ContentType.Text, String.Format("{{{0}}}",String.Join(",\n",settings)));
                m_Package.PushItem(filename, jsonItem);
                var binary = m_Engine.PublishingContext.RenderedItem.AddBinary(jsonItem.GetAsStream(), filename, sg, variant, comp, mimetype);
                files.Add(Json.Encode(binary.FilePath));
            }
            if (allComponent == null)
            {
                throw new Exception(String.Format("Cannot find '_All' component. Please ensure that this component is in the {0} folder.", folder.WebDavUrl));
            }
            Item filesItem = m_Package.CreateStringItem(ContentType.Text, String.Format("{{\"files\":[{0}]}}",String.Join(",",files)));
            m_Package.PushItem("Output", filesItem);
            //We create a special config file _all.js which contains the path of all other config files published
            m_Engine.PublishingContext.RenderedItem.AddBinary(filesItem.GetAsStream(), "_all.js", sg, variant, allComponent, mimetype);
        }
    }

}

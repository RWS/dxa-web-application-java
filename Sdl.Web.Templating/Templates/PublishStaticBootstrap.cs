using System;
using System.Text;
using Sdl.Web.Tridion.Common;
using System.Collections.Generic;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;

namespace Sdl.Web.Tridion.Templates
{
    /// <summary>
    /// Publishes the _all.json file which is used to load all other JSON configuration/resource/mapping files
    /// </summary>
    [TcmTemplateTitle("Publish Static Bootstrap")]
    public class PublishStaticBootstrap : TemplateBase
    {
        // template builder log
        private static readonly TemplatingLogger Log = TemplatingLogger.GetLogger(typeof(PublishStaticBootstrap));

        // json content in page
        private const string JsonOutputFormat = "{{\"name\":\"Publish Static Bootstrap\",\"status\":\"Success\",\"files\":[{0}]}}";

        //private string _moduleRoot = String.Empty;

        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);
            
            //The core configuration component should be the one being processed by the template
            Component coreConfigComponent = GetComponent();
            StructureGroup sg = GetSystemStructureGroup();

            //Publish the boostrap list, this is used by the web application to load in all other static files
            List<string> files = GetBootstrapFiles();
            PublishBootstrapJson(files, coreConfigComponent, sg, "statics-");

            StringBuilder publishedFiles = new StringBuilder();
            foreach (string file in files)
            {
                publishedFiles.AppendCommaSeparated(file);
                Log.Info("Published " + file);
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

        private List<string> GetBootstrapFiles()
        {
            List<string> files = new List<string>();
            foreach (KeyValuePair<string, Item> item in Package.GetEntries())
            {
                if (item.Key.EndsWith(BootstrapFilename + JsonExtension))
                {
                    files.Add(JsonEncode(item.Key));
                }
            }
            return files;
        }

    }
}

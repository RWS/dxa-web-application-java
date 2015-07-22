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
                Logger.Info("Published " + file);
            }

            // append json result to output
            string json = String.Format(JsonOutputFormat, publishedFiles);
            Item outputItem = package.GetByName(Package.OutputName);
            if (outputItem != null)
            {
                package.Remove(outputItem);
                string output = outputItem.GetAsString();
                if (output.StartsWith("["))
                {
                    // insert new json object
                    json = String.Format("{0},{1}{2}]", output.TrimEnd(']'), Environment.NewLine, json);
                }
                else
                {
                    // append new json object
                    json = String.Format("[{0},{1}{2}]", output, Environment.NewLine, json);
                }
            }
            package.PushItem(Package.OutputName, package.CreateStringItem(ContentType.Text, json));
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

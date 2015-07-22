using System;
using System.Text;
using Sdl.Web.Tridion.Common;
using System.Collections.Generic;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;

namespace Sdl.Web.Tridion.Templates
{
    /// <summary>
    /// Publish resource JSON files (one per module). A module configuration can link to 
    /// multiple resource components - these are merged into a single JSON file
    /// </summary>
    [TcmTemplateTitle("Publish Resources")]
    public class PublishResources : TemplateBase
    {
        // template builder log
        private static readonly TemplatingLogger Log = TemplatingLogger.GetLogger(typeof(PublishStaticBootstrap));

        // json content in page
        private const string JsonOutputFormat = "{{\"name\":\"Publish Resources\",\"status\":\"Success\",\"files\":[{0}]}}";

        //private string _moduleRoot;

        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);
            
            //The core configuration component should be the one being processed by the template
            Component coreConfigComponent = GetComponent();
            StructureGroup sg = GetSystemStructureGroup("resources");
            //_moduleRoot = GetModulesRoot(coreConfigComponent);

            //Get all the active modules
            Dictionary<string, Component> moduleComponents = GetActiveModules(coreConfigComponent);
            List<string> filesCreated = new List<string>();
            
            //For each active module, publish the config and add the filename(s) to the bootstrap list
            foreach (KeyValuePair<string, Component> module in moduleComponents)
            {
                filesCreated.Add(ProcessModule(module.Key, module.Value, sg));
            }

            //Publish the boostrap list, this is used by the web application to load in all other resource files
            PublishBootstrapJson(filesCreated, coreConfigComponent, sg, "resource-");

            StringBuilder publishedFiles = new StringBuilder();
            foreach (string file in filesCreated)
            {
                if (!String.IsNullOrEmpty(file))
                {
                    publishedFiles.AppendCommaSeparated(file);
                    Log.Info("Published " + file);                    
                }
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

        protected string ProcessModule(string moduleName, Component module, StructureGroup sg)
        {
            Dictionary<string, string> data = new Dictionary<string, string>();
            ItemFields fields = new ItemFields(module.Content, module.Schema);

            foreach (Component configComp in fields.GetComponentValues("resource"))
            {
                data = MergeData(data, ReadComponentData(configComp));
            }
            return PublishJsonData(data, module, moduleName, "resource", sg);
        }
    }
}

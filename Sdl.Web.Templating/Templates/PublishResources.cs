using System;
using System.Collections.Generic;
using Sdl.Web.Tridion.Common;
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
        //private string _moduleRoot;

        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);
            
            //The core configuration component should be the one being processed by the template
            var coreConfigComponent = GetComponent();
            var sg = GetSystemStructureGroup("resources");
            //_moduleRoot = GetModulesRoot(coreConfigComponent);

            //Get all the active modules
            Dictionary<string, Component> moduleComponents = GetActiveModules(coreConfigComponent);
            List<string> filesCreated = new List<string>();
            
            //For each active module, publish the config and add the filename(s) to the bootstrap list
            foreach (var module in moduleComponents)
            {
                filesCreated.Add(ProcessModule(module.Key, module.Value, sg));
            }

            //Publish the boostrap list, this is used by the web application to load in all other resource files
            PublishBootstrapJson(filesCreated, coreConfigComponent, sg, "resource-");
        }

        protected string ProcessModule(string moduleName, Component module, StructureGroup sg)
        {
            Dictionary<string, string> data = new Dictionary<string, string>();
            ItemFields fields = new ItemFields(module.Content, module.Schema);

            foreach (var configComp in fields.GetComponentValues("resource"))
            {
                data = MergeData(data, ReadComponentData(configComp));
            }
            return PublishJsonData(data, module, moduleName, "resource", sg);
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;
using Sdl.Web.Templating.ExtensionMethods;

namespace Sdl.Web.Templating
{
    [TcmTemplateTitle("Publish Resources")]
    public class PublishResources : TemplateBase.TemplateBase
    {
        private string _moduleRoot = string.Empty;

        public override void Transform(Engine engine, Package package)
        {
            this.Initialize(engine, package);
            //The core configuration component should be the one being processed by the template
            var coreConfigComponent = this.GetComponent();
            var sg = GetSystemStructureGroup("resources");
            _moduleRoot = GetModulesRoot(coreConfigComponent);
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
            List<string> data = new List<string>();
            ItemFields fields = new ItemFields(module.Content, module.Schema);
            foreach (var configComp in fields.GetComponentValues("resource"))
            {
                data.AddRange(ReadComponentData(configComp));
            }
            return PublishJsonData(data, module, moduleName, sg);
        }
    }
}

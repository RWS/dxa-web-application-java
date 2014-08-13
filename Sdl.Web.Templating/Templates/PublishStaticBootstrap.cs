using System;
using System.Collections.Generic;
using System.Web.Helpers;
using Sdl.Web.Tridion.Common;
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
        //private string _moduleRoot = String.Empty;

        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);
            
            //The core configuration component should be the one being processed by the template
            var coreConfigComponent = GetComponent();
            var sg = GetSystemStructureGroup();

            //Publish the boostrap list, this is used by the web application to load in all other static files
            PublishBootstrapJson(GetBootstrapFiles(), coreConfigComponent, sg, "statics-");
        }

        private List<string> GetBootstrapFiles()
        {
            List<string> files = new List<string>();
            foreach (var item in Package.GetEntries())
            {
                if (item.Key.EndsWith(BootstrapFilename + JsonExtension))
                {
                    files.Add(Json.Encode(item.Key));
                }
            }
            return files;
        }

    }
}

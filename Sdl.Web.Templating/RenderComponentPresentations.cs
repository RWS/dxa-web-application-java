using Sdl.Web.ContentManagement.Templating;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;

namespace Sdl.Web.Templating
{
    [TcmTemplateTitle("Render Component Presentations")]
    public class RenderComponentPresentations : TemplateBase
    {
        public override void Transform(Engine engine, Package package)
        {
            this.Initialize(engine, package);
            var page = this.GetPage();
            StringBuilder output = new StringBuilder();
            if (page != null)
            {
                foreach (var cp in page.ComponentPresentations)
                {
                    output.AppendLine(engine.RenderComponentPresentation(cp.Component.Id, cp.ComponentTemplate.Id));
                }
            }
            package.PushItem("Output", package.CreateStringItem(ContentType.Text, output.ToString()));
        }
    }
}

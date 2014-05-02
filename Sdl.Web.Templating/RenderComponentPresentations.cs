using System.Text;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;

namespace Sdl.Web.Templating
{
    [TcmTemplateTitle("Render Component Presentations")]
    public class RenderComponentPresentations : TemplateBase.TemplateBase
    {
        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);
            var page = GetPage();
            StringBuilder output = new StringBuilder();
            if (page != null)
            {
                foreach (var cp in page.ComponentPresentations)
                {
                    output.AppendLine(engine.RenderComponentPresentation(cp.Component.Id, cp.ComponentTemplate.Id));
                }
            }

            package.PushItem(Package.OutputName, package.CreateStringItem(ContentType.Text, output.ToString()));
        }
    }
}

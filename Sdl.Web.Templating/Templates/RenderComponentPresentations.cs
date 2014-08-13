using Sdl.Web.Tridion.Common;
using System.Text;
using System.Text.RegularExpressions;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;

namespace Sdl.Web.Tridion.Templates
{
    /// <summary>
    /// Renders the component presentations to the package output. Useful when there is no page layout for publishing data
    /// </summary>
    [TcmTemplateTitle("Render Component Presentations")]
    public class RenderComponentPresentations : TemplateBase
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
                    output.AppendLine(RemoveTcdl(engine.RenderComponentPresentation(cp.Component.Id, cp.ComponentTemplate.Id)));
                }
            }
            package.PushItem(Package.OutputName, package.CreateStringItem(ContentType.Text, output.ToString()));
        }

        private static string RemoveTcdl(string p)
        {
            p = Regex.Replace(p, "<tcdl:ComponentPresentation[^>]+>", "");
            return p.Replace("</tcdl:ComponentPresentation>", "");
        }
    }
}

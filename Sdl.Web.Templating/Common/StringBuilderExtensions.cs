using System;
using System.Text;

namespace Sdl.Web.Tridion.Common
{
    public static class StringBuilderExtensions
    {
        public static StringBuilder AppendCommaSeparated(this StringBuilder sb, string format, params object[] arguments)
        {
            if (sb.Length > 0)
            {
                sb.Append(",");
            }
            sb.AppendFormat(format, arguments);
            return sb;
        }
    }
}

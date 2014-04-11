using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;

namespace Sdl.Web.ContentManagement.ExtensionMethods
{
    public static class GeneralExtensionMethods
    {
        public static string GetIso8601Date(this DateTime date)
        {
            return date > DateTime.MinValue ? date.ToString("s", CultureInfo.InvariantCulture) + "Z" : null;
        }
    }
}

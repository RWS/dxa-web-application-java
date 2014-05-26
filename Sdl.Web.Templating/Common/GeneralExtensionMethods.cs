using System;
using System.Globalization;

namespace Sdl.Web.Tridion.Common
{
    public static class GeneralExtensionMethods
    {
        public static string GetIso8601Date(this DateTime date)
        {
            return date > DateTime.MinValue ? date.ToString("s", CultureInfo.InvariantCulture) + "Z" : null;
        }
    }
}

using System;
using System.Globalization;
using System.IO;
using System.IO.Compression;

namespace Sdl.Web.Tridion.Common
{
    public static class GeneralExtensionMethods
    {
        public static string GetIso8601Date(this DateTime date)
        {
            return date > DateTime.MinValue ? date.ToString("s", CultureInfo.InvariantCulture) + "Z" : null;
        }

        public static void ExtractToDirectory(this ZipArchive zip, string destinationDirectoryName, bool overwrite)
        {
            foreach (var entry in zip.Entries)
            {
                var path = Path.Combine(destinationDirectoryName, entry.FullName);
                string directory = Path.GetDirectoryName(path);
                if (!Directory.Exists(directory))
                {
                    Directory.CreateDirectory(Path.GetDirectoryName(path));
                }
                if (!String.IsNullOrEmpty(entry.Name))
                {
                    entry.ExtractToFile(path, true);
                }
            }
        }
    }
}

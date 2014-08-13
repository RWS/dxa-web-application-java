using System;
using System.Text.RegularExpressions;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;

namespace Sdl.Web.Tridion.Common
{
    /// <summary>
    /// Static Helper methods
    /// </summary>
    public static class Utility
    {
        public static string GetKeyFromTaxonomy(Category taxonomy)
        {
            var key = taxonomy.XmlName;
            return key.Substring(0, 1).ToLower() + key.Substring(1);
        }

        public static string GetKeyFromTemplate(ComponentTemplate template)
        {
            var key = Regex.Replace(template.Title, @"[\[\]\s\.]", "");
            return key.Substring(0, 1).ToLower() + key.Substring(1);
        }

        public static string GetKeyFromSchema(Schema schema)
        {
            var key = schema.RootElementName;
            if (String.IsNullOrEmpty(key))
            {
                key = Regex.Replace(schema.Title.Trim(), @"[^A-Za-z0-9.]+", "");
            }
            return key.Substring(0, 1).ToLower() + key.Substring(1);
        }
    }
}

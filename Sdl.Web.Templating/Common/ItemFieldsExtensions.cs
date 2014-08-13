using System;
using System.Collections.Generic;
using System.Linq;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;

namespace Sdl.Web.Tridion.Common
{
    public static class ItemFieldsExtensions
    {
        public static double GetNumberValue(this ItemFields fields, string fieldName)
        {
            return fields.GetNumberValues(fieldName).FirstOrDefault();
        }

        public static IEnumerable<double> GetNumberValues(this ItemFields fields, string fieldName)
        {
            return
                null != fields && fields.Contains(fieldName)
                    ? (fields[fieldName] as NumberField).Values
                    : new double[0];
        }

        public static Keyword GetKeywordValue(this ItemFields fields, string fieldName)
        {
            return fields.GetKeywordValues(fieldName).FirstOrDefault();
        }

        public static IEnumerable<Keyword> GetKeywordValues(this ItemFields fields, string fieldName)
        {
            return
                null != fields && fields.Contains(fieldName)
                    ? (fields[fieldName] as KeywordField).Values
                    : Enumerable.Empty<Keyword>();
        }

        public static Component GetComponentValue(this ItemFields fields, string fieldName)
        {
            return fields.GetComponentValues(fieldName).FirstOrDefault();
        }

        public static IEnumerable<Component> GetComponentValues(this ItemFields fields, string fieldName)
        {
            return
                null != fields && fields.Contains(fieldName)
                    ? (fields[fieldName] as ComponentLinkField).Values
                    : Enumerable.Empty<Component>();
        }

        public static string GetExternalLink(this ItemFields fields, string fieldName)
        {
            return fields.GetExternalLinks(fieldName).FirstOrDefault() ?? string.Empty;
        }

        public static IEnumerable<string> GetExternalLinks(this ItemFields fields, string fieldName)
        {
            return
                null != fields && fields.Contains(fieldName)
                    ? (fields[fieldName] as ExternalLinkField).Values
                    : new string[0];
        }

        public static Component GetMultimediaLink(this ItemFields fields, string fieldName)
        {
            return fields.GetMultimediaLinks(fieldName).FirstOrDefault();
        }

        public static IEnumerable<Component> GetMultimediaLinks(this ItemFields fields, string fieldName)
        {
            return
                null != fields && fields.Contains(fieldName)
                    ? (fields[fieldName] as MultimediaLinkField).Values
                    : Enumerable.Empty<Component>();
        }

        public static ItemFields GetEmbeddedField(this ItemFields fields, string fieldName)
        {
            return fields.GetEmbeddedFields(fieldName).FirstOrDefault();
        }

        public static IEnumerable<ItemFields> GetEmbeddedFields(this ItemFields fields, string fieldName)
        {
            return
                null != fields && fields.Contains(fieldName)
                    ? (fields[fieldName] as EmbeddedSchemaField).Values
                    : Enumerable.Empty<ItemFields>();
        }

        public static string GetTextValue(this ItemFields fields, string fieldName)
        {
            return GetTextValues(fields, fieldName).FirstOrDefault() ?? string.Empty;
        }

        public static IEnumerable<string> GetTextValues(this ItemFields fields, string fieldName)
        {
            return
                null != fields && fields.Contains(fieldName)
                    ? (fields[fieldName] as TextField).Values
                    : new string[0];
        }

        public static DateTime? GetDateValue(this ItemFields fields, string fieldName = "date")
        {
            return fields.GetDateValues(fieldName).FirstOrDefault();
        }

        public static IEnumerable<DateTime?> GetDateValues(this ItemFields fields, string fieldName = "date")
        {
            return
                null != fields && fields.Contains(fieldName)
                    ? (fields[fieldName] as DateField).Values.Select(d => d == DateTime.MinValue ? null : (DateTime?)d)
                    : new DateTime?[0];
        }

        public static int GetFieldValueCount(this ItemFields fields, string fieldName)
        {
            if (null == fields)
            {
                return 0;
            }

            var field = fields[fieldName];

            return
                field is ComponentLinkField
                    ? (field as ComponentLinkField).Values.Count
                    : field is TextField
                        ? (field as TextField).Values.Count
                        : field is EmbeddedSchemaField
                            ? (field as EmbeddedSchemaField).Values.Count
                            : 0;
        }

        /// <summary>
        /// Manual unification of different field types logic to overcome native tridion implementation shortcoming,
        /// which is not polymorphic.
        /// </summary>
        static readonly IDictionary<Type, Func<ItemFields, string, string>> ValueResolver =
            new Dictionary<Type, Func<ItemFields, string, string>> {
            { typeof(KeywordField), (fields, name) => fields.GetKeywordValues(name).Select(k => k.Title).FirstOrDefault() },
            { typeof(ComponentLinkField), (fields, name) => fields.GetComponentValues(name).Select(c => c.Id).FirstOrDefault() },
            { typeof(ExternalLinkField), (fields, name) => fields.GetExternalLinks(name).FirstOrDefault() },
            { typeof(MultimediaLinkField), (fields, name) => fields.GetMultimediaLinks(name).Select(mc => mc.Title).FirstOrDefault() },
            { typeof(DateField), (fields, name) => ((DateTime)fields.GetDateValues(name).FirstOrDefault()).ToString("yyyy-MM-dd HH:mm:ss") }
        };

        /// <summary>
        /// Gets a sensible string represntation of a field.
        /// </summary>
        public static string GetSingleFieldValue(this ItemFields fields, string fieldName)
        {
            ItemField field;
            Type fieldType;

            return
                null == fields
                || !fields.Contains(fieldName)
                    ? String.Empty
                    : ValueResolver.ContainsKey((fieldType = (field = fields[fieldName]).GetType()))
                        ? ValueResolver[fieldType](fields, fieldName) ?? String.Empty
                        : field.ToString();
        }
    }
}

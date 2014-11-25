using System;
using System.Linq;
using System.Text.RegularExpressions;
using System.Xml;
using Tridion;
using Tridion.ContentManager;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.Templating;
//using Tridion.TopologyManager.Client;

namespace Sdl.Web.Tridion.Common
{
    /// <summary>
    /// Static Helper methods
    /// </summary>
    public static class Utility
    {
        public const string SiteEditApplicationId = "SiteEdit";

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

        public static TargetType GetTargetType(PublishingContext publishingContext)
        {
            PublicationTarget publicationTarget = publishingContext.PublicationTarget;
            // Fixme (when API is fixed): remove this function and instead use: "TargetType targetType = publishingContext.TargetType";
            TargetType targetType = (publicationTarget != null) ? publicationTarget.TargetTypes.FirstOrDefault() : null;
            return targetType;
        }

        #region 8.0 API
        //public static bool IsPublishTargetXpmEnabled(PublishingContext publishingContext)
        //{
        //    bool enabled = false;
        //    if (IsUsingTopologyManager(publishingContext))
        //    {
        //        enabled = IsTargetTypeXpmEnabled(publishingContext);
        //    }
        //    else
        //    {
        //        enabled = IsPublicationTargetXpmEnabled(publishingContext.PublicationTarget);
        //    }
        //    return enabled;
        //}

        //public static bool IsUsingTopologyManager(PublishingContext publishingContext)
        //{
        //    bool isUsingTopologyManager = false;
        //    TargetType targetType = GetTargetType(publishingContext);
        //    if (targetType != null)
        //    {
        //        // If the target type has a business process type, then topology manager must be used for publishing
        //        if (targetType.BusinessProcessType != null)
        //        {
        //            isUsingTopologyManager = true;
        //        }
        //    }
        //    return isUsingTopologyManager;
        //}

        //private static bool IsTargetTypeXpmEnabled(PublishingContext publishingContext)
        //{
        //    bool enabled = false;
        //    TargetType targetType = GetTargetType(publishingContext);
        //    if (targetType != null)
        //    {
        //        RepositoryLocalObject item = publishingContext.ResolvedItem.Item as RepositoryLocalObject;
        //        if (item != null)
        //        {
        //            Publication currentPublication = (Publication)item.ContextRepository;
        //            if (targetType.IsPreviewCapable(currentPublication))
        //            {
        //                enabled = true;
        //            }
        //        }
        //    }
        //    return enabled;
        //}

        // when using 8.0 API, IsPublicationTargetXpmEnabled(PublicationTarget) can be made private, so we are forced to use IsPublishTargetXpmEnabled(PublishingContext)
        //private static bool IsPublicationTargetXpmEnabled(PublicationTarget publicationTarget)
        #endregion

        public static bool IsPublicationTargetXpmEnabled(PublicationTarget publicationTarget)
        {
            bool enabled = false;
            if (publicationTarget != null)
            {
                ApplicationData appData = publicationTarget.LoadApplicationData(SiteEditApplicationId);
                if (appData != null)
                {
                    ApplicationDataAdapter ada = new ApplicationDataAdapter(appData);
                    XmlElement appDataXml = ada.GetAs<XmlElement>();
                    if (appDataXml != null)
                    {
                        if (appDataXml.SelectSingleNode("self::se:configuration/se:PublicationTarget[se:EnableSiteEdit = 'true']", GetSeNamespaceManager()) != null)
                        {
                            enabled = true;
                        }
                    }
                }
            }
            return enabled;
        }

        private static XmlNamespaceManager _ns;
        private static XmlNamespaceManager GetSeNamespaceManager()
        {
            if (_ns == null)
            {
                _ns = new XmlNamespaceManager(new NameTable());
                _ns.AddNamespace("se", "http://www.sdltridion.com/2011/SiteEdit");
                _ns.AddNamespace(Constants.XlinkPrefix, Constants.XlinkNamespace);
            }
            return _ns;
        }
    }
}

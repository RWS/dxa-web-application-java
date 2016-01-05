using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using Tridion.ContentManager.Templating;
using Tridion.TopologyManager.Client;

namespace Sdl.Web.Tridion
{
    /// <summary>
    /// Wrapper class for access to Topology Manager (to prevent assembly binding errors when run on SDL Tridion 2013 SP1, which doesn't have Tridion.TopologyManager.Client).
    /// </summary>
    internal static class TopologyManager
    {
        private static readonly TemplatingLogger _logger = TemplatingLogger.GetLogger(typeof(TopologyManager));
        private static TopologyManagerClient _topologyManagerClient;
        private static CmEnvironmentData _cmEnvironment;

        internal static string GetCmWebsiteUrl()
        {
            if (_cmEnvironment == null)
            {
                _cmEnvironment = TopologyManagerClient.CmEnvironments.Where(env => env.Id == TopologyManagerClient.ContentManagerEnvironmentId).FirstOrDefault();
                if (_cmEnvironment == null)
                {
                    throw new Exception("Unable to obtain CM Environment Data from Topology Manager. CM Environment ID: " + TopologyManagerClient.ContentManagerEnvironmentId);
                }
            }

            return _cmEnvironment.WebsiteRootUrl;
        }

        private static TopologyManagerClient TopologyManagerClient
        {
            get
            {
                if (_topologyManagerClient == null)
                {
                    _topologyManagerClient = new TopologyManagerClient
                    {
                        Credentials = CredentialCache.DefaultNetworkCredentials
                    };
                }
                return _topologyManagerClient;
            }
        }
    }
}

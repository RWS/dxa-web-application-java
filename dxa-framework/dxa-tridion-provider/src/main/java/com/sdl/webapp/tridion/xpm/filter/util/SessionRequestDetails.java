package com.sdl.webapp.tridion.xpm.filter.util;

import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.sdl.web.preview.util.PreviewSessionClaims.*;
import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Incorporated this class from the udp library in order to update it with the Jakarta Servlet API.
 * Class that holds simple details about an incoming requests that will be used by our {@code PageContentFilter} and
 * {@code BinaryContentFilter}.
 */
@Getter
public class SessionRequestDetails {

    private static final Logger LOG = LoggerFactory.getLogger(SessionRequestDetails.class);

    /**
     * The fullURL property as it's supposed to be used by our content filters.
     * -- GETTER --
     *  Gets the fullURL property that will be used by the content filters.
     */
    private String fullURL;

    /**
     * The realPath property as it's supposed to be used by our content filters.
     * -- GETTER --
     *  Gets the realPath property that will be used by the content filters.
     */
    private String realPath;

    /**
     * The rootPath property as it's supposed to be used by our content filters.
     * -- GETTER --
     *  Gets the rootPath property that will be used by the content filters.
     */
    private String rootPath;

    /**
     * Tells if we should forward to the session container or just continue the pipeline.
     * -- GETTER --
     *  Tells if the content filters will be doing forwarding towards the session content of just
     *  continue the pipeline.
     *
     */
    private boolean forwarding = true;

    /**
     * Constructor that looks into the current request and extracts the needed information. After this, it overrides
     * the default information with information that might be defined in the current claimStore.
     */

    public SessionRequestDetails(HttpServletRequest httpRequest) {
        // get the default values for our properties
        fullURL = httpRequest.getRequestURL().toString();
        final String filePath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        ServletContext servletContext = httpRequest.getServletContext();
        realPath = decode(servletContext.getRealPath(filePath), UTF_8);
        rootPath = decode(servletContext.getRealPath("/"), UTF_8);

        ClaimStore claimStore = AmbientDataContext.getCurrentClaimStore();
        if (claimStore != null) {
            // check if the fullURL is redefined in the claimStore.
            if (claimStore.contains(REQUEST_FULL_URL)) {
                LOG.debug("Using custom defined raw URL instead of the original request raw url: {}.", fullURL);
                fullURL = claimStore.get(REQUEST_FULL_URL, String.class);
            }

            // check if the realPath is redefined in the claimStore.
            if (claimStore.contains(REQUEST_REAL_PATH)) {
                LOG.debug("Using custom defined real path instead of the original request real path: {}.", realPath);
                realPath = claimStore.get(REQUEST_REAL_PATH, String.class);
            }

            // check if the rootPath is redefined in the claimStore.
            if (claimStore.contains(REQUEST_ROOT_PATH)) {
                LOG.debug("Using custom defined root path instead of the original request root path: {}.", rootPath);
                rootPath = claimStore.get(REQUEST_ROOT_PATH, String.class);
            }

            // check if the forwarding property is redefined in the claimStore.
            if (claimStore.contains(REQUEST_FORWARDING)) {
                final Object forwardingClaimValue = claimStore.get(REQUEST_FORWARDING);

                // make sure it's in the format we're interested in
                if (forwardingClaimValue instanceof Boolean) {
                    LOG.debug("Forwarding redefined to: {}.", forwardingClaimValue);
                    forwarding = (Boolean) forwardingClaimValue;
                }
            }
        }
        // In case of WAS for eg.
        if (rootPath != null && rootPath.endsWith(File.separator)) {
            rootPath = rootPath.substring(0, rootPath.length() - 1);
        }
    }
}

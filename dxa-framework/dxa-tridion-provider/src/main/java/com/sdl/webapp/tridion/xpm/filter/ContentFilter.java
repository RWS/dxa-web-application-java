package com.sdl.webapp.tridion.xpm.filter;

import com.sdl.web.preview.model.PreviewSession;
import com.sdl.web.preview.util.PreviewSessionClaims;
import com.sdl.web.preview.util.SessionHandlingException;
import com.sdl.webapp.tridion.xpm.filter.util.SessionRequestDetails;
import com.tridion.ambientdata.AmbientDataConfig;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
import com.tridion.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Incorporated this class from the udp library in order to update it with the Jakarta Servlet API.
 * Filter used to render session content that was modified in this session.
 */
public abstract class ContentFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ContentFilter.class);

    private List<String> excludedPaths = new ArrayList<>();

    @Override
    public final void init(FilterConfig filterConfig) throws ServletException {
        try {
            AmbientDataConfig config = AmbientDataConfig.getAmbientDataConfig();
            List<String> toExclude = config.getExcludedPaths();
            if (toExclude != null && !toExclude.isEmpty()) {
                this.excludedPaths = toExclude;
            }
        }
        catch (ConfigurationException e) {
            LOG.warn("Could not get excluded paths from Ambient Data Framework configuration. Will not exclude " +
                     "any paths.");
            LOG.debug("", e);
        }
        try {
            doInit();
        }
        catch (SessionHandlingException e) {
            LOG.error("Error initializing content filter.", e);
            throw new ServletException("Error initializing content filter.", e);
        }
    }

    /**
     * Use this method to configure particular implementation.
     * @throws SessionHandlingException in case of initialization exception
     */
    protected abstract void doInit() throws SessionHandlingException;

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
     * javax.servlet.FilterChain)
     */
    @Override
    public final void doFilter(final ServletRequest servletRequest,
                               final ServletResponse servletResponse,
                               final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        final HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        ServletContext servletContext = servletRequest.getServletContext();

        // get the relative path of this request to check if maybe ADF is excluded on this path
        final String relativePath = httpRequest.getRequestURI();

        if (!isExcludedPath(relativePath)) { // make sure we are allowed to process this path
            LOG.debug("Processing request for '{}'.", relativePath);
            // get some details about the current request(including overrides dictated by definitions in the ClaimStore)
            final SessionRequestDetails srd = new SessionRequestDetails(httpRequest);
            // log some debug information
            LOG.debug("Raw URL: {}.", srd.getFullURL());
            LOG.debug("Real path: {}.", srd.getRealPath());
            LOG.debug("Root path: {}.", srd.getRootPath());

            // handle our request and put session content on disk
            final String relativeURL = handleRequest(srd.getFullURL(), srd.getRealPath(), srd.getRootPath());

            if (relativeURL != null) { // the content handler placed the session content on disk
                LOG.debug("Session content is now at relativeURL: {}.", relativeURL);

                // let's also store a claim to say the relative path where we have the session content
                final ClaimStore claimStore = AmbientDataContext.getCurrentClaimStore();
                if (claimStore != null) {
                    claimStore.put(PreviewSessionClaims.SESSION_FILE_RELATIVE_PATH, relativeURL);
                }

                // now let's check if we should forward or continue the filter chain
                if (srd.isForwarding()) { /* let's forward the request to the stored session content */
                    // get the request dispatcher for our relative url
                    final RequestDispatcher rd = servletContext.getRequestDispatcher(relativeURL);
                    if (rd != null) {
                        LOG.debug("Forwarding the request");

                        rd.forward(httpRequest, httpResponse);
                    }
                    else {
                        LOG.debug("No requestDispatcher for relativeURL: {}.", relativeURL);
                    }
                }
                else { // we do not forward but just continue the chain
                    LOG.debug("Not forwarding but moving further into the chain");
                    /* go to the next stop in the chain */
                    filterChain.doFilter(httpRequest, httpResponse);
                }
            }
            else {
                LOG.debug("No session content found. Moving further into the chain");
                /* go to the next stop in the chain */
                filterChain.doFilter(httpRequest, httpResponse);
            }
        }
        else { // this relative path should not be processed by us at all
            LOG.debug("Path '{}' will not be processed as it matches the configured exclusion.", relativePath);
            LOG.debug("Moving further into the chain");
            /* go to the next stop in the chain */
            filterChain.doFilter(httpRequest, httpResponse);
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        // not implemented by us.
    }

    /**
     * Handles a request to a resource.
     *
     * @param fullURL The full url of the request.
     * @param realPath The real file path where the request should go.
     * @param rootPath The root path of the website.
     * @return The relative path where we find the session content.
     */
    protected abstract String handleRequest(String fullURL, String realPath, String rootPath) throws
            IOException;

    /**
     * Tells is a relative path is excluded from the ADF processing.
     *
     * @param path The path to be checked.
     * @return True if the request to the given path is excluded from ADF processing.
     */
    private boolean isExcludedPath(String path) {
        return excludedPaths.stream().anyMatch(path::startsWith);
    }

    protected abstract PreviewSession obtainPreviewSession();

    protected ClaimStore getClaimStore() {
        return AmbientDataContext.getCurrentClaimStore();
    }
}

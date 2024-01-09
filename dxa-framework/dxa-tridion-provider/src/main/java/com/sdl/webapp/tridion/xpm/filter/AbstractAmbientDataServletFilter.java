package com.sdl.webapp.tridion.xpm.filter;

import com.sdl.web.ambient.api.BadRequestException;
import com.tridion.ambientdata.AmbientDataConfig;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.AmbientDataException;
import com.tridion.ambientdata.CookieConfig;
import com.tridion.ambientdata.claimstore.ClaimStore;
import com.tridion.ambientdata.claimstore.ClaimStoreUtil;
import com.tridion.ambientdata.claimstore.ClaimType;
import com.tridion.ambientdata.claimstore.Constants;
import com.tridion.ambientdata.claimstore.DefaultClaimStore;
import com.tridion.ambientdata.claimstore.JavaClaimStore;
import com.tridion.ambientdata.claimstore.cookie.ClaimCookieDeserializer;
import com.tridion.ambientdata.claimstore.cookie.ClaimsCookie;
import com.tridion.ambientdata.claimstore.providers.ClaimStoreProvider;
import com.tridion.ambientdata.claimstore.providers.ClaimStoreProviderFactory;
import com.tridion.ambientdata.configuration.CartridgeCategory;
import com.tridion.ambientdata.processing.HTTPHeaderProcessor;
import com.tridion.ambientdata.web.WebClaims;
import com.tridion.ambientdata.web.WebContext;
import com.tridion.ambientdata.web.filter.WhiteListFilter;
import com.tridion.ambientdata.web.filter.WhiteListFilterFactory;
import com.tridion.configuration.ConfigurationException;
import com.tridion.security.UnauthorizedException;
import com.tridion.util.StringUtils;
import com.tridion.util.TridionReflectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.tridion.ambientdata.AmbientDataContext.getAmbientDataConfig;

/**
 * Incorporated this class from the udp library in order to update it with the Jakarta Servlet API.
 * Abstract filter that is called by the application server when a request is received.
 * <p/>
 * <p>To use the ambient data framework in a web application,
 * you have to configure this filter class in the {@code web.xml} deployment
 * descriptor, as in the following example:</p>
 * <pre style="color: navy;">
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;web-app version="2.5" xmlns="http://java.sun.com/xml/ns/javaee"
 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 * xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"&gt;
 * &lt;filter&gt;
 * &lt;description&gt;Servlet filter for the ambient data framework.&lt;/description&gt;
 * &lt;filter-name&gt;Ambient Data Framework&lt;/filter-name&gt;
 * &lt;filter-class&gt;com.tridion.ambientdata.web.AmbientDataServletFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 * &lt;filter-mapping&gt;
 * &lt;filter-name&gt;Ambient Data Framework&lt;/filter-name&gt;
 * &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;dispatcher&gt;REQUEST&lt;/dispatcher&gt;
 * &lt;/filter-mapping&gt;
 * &lt;welcome-file-list&gt;
 * &lt;welcome-file&gt;index.jsp&lt;/welcome-file&gt;
 * &lt;/welcome-file-list&gt;
 * &lt;/web-app&gt;
 * </pre>
 */
public abstract class AbstractAmbientDataServletFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAmbientDataServletFilter.class);

    /**
     * The Initial Capacity for constants map.
     */
    private static final int INITIAL_CAPACITY = 12;

    /**
     * Key used to store claim store as a session attribute.
     */
    private static final String SESSION_KEY_CLAIMSTORE = "session-" + ClaimStore.class.getName();

    /**
     * Key used to store claim store as a request attribute.
     */
    private static final String REQUEST_KEY_CLAIMSTORE = "request-" + ClaimStore.class.getName();

    private static final String FILTER_CARTRIDGE_CATEGORY_INIT_PARAM = "filterCartridgeCategory";
    private static final String ADF_SERVICE = "adfService";
    private static final String[] EMPTY = new String[0];

    private WhiteListFilter whiteListFilter;

    private String instanceId;
    private CookieConfig sessionCookieConfig;
    private CookieConfig trackingCookieConfig;
    private String adfCookiePrefix;
    private boolean isADFCookieEnabled;
    private List<String> excludedPaths;
    private List<String> globallyAcceptedClaims;
    private Boolean cookieClaimDefaultValue;
    private URI cookieClaimName;
    private HTTPHeaderProcessor httpHeaderProcessor;

    private ClaimStoreProvider claimStoreProvider;

    private AmbientDataContext ambientDataContext;
    private boolean cartridgesInstalled;
    private boolean adfService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.debug("Initializing Ambient Data Framework filter");

        try {
            // Set if this is running REST service
            setADFService(getBooleanFilterParameter(filterConfig, ADF_SERVICE));
            // Set ambient data context
            ambientDataContext = new WebContext(
                    getBooleanFilterParameter(filterConfig, FILTER_CARTRIDGE_CATEGORY_INIT_PARAM) ?
                            CartridgeCategory.SYSTEM : CartridgeCategory.ALL);

            AmbientDataContext.setContext(ambientDataContext);
            AmbientDataConfig config = getAmbientDataConfig();

            if (config != null) {
                initializeEngine();

                whiteListFilter = WhiteListFilterFactory.newWhiteListFilter();
                claimStoreProvider = ClaimStoreProviderFactory.newClaimStoreProvider(config,
                        ClaimStoreProviderFactory.DEFAULT_JAVA_CLAIM_STORE_PROVIDER);
                sessionCookieConfig = config.getCookieConfiguration(CookieConfig.CookieType.SESSION);
                trackingCookieConfig = config.getCookieConfiguration(CookieConfig.CookieType.TRACKING);
                adfCookiePrefix = config.getADFCookiePrefix();

                instanceId = config.getInstanceID();
                excludedPaths = config.getExcludedPaths();
                globallyAcceptedClaims = config.getGloballyAcceptedClaims();
                isADFCookieEnabled = config.isAcceptingForwardedClaims();

                cookieClaimDefaultValue = config.getDefaultCookieClaimValue();
                cookieClaimName = config.getCookieClaimName();

                httpHeaderProcessor = new HTTPHeaderProcessor();
            }
            else {
                throw new ConfigurationException("Ambient Data Framework configuration was not properly initialized!");
            }

            if (!getAmbientDataConfig().getCartridgeConfigurations().isEmpty()) {
                cartridgesInstalled = true;
            }
        }
        catch (AmbientDataException | TridionReflectionException | ConfigurationException e) {
            throw new ServletException("Error while initializing ambient data framework", e);
        }
    }

    protected boolean isRequestPathExcluded(String requestURL) {
        for (String path : excludedPaths) {
            if (requestURL.startsWith(path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        LOG.debug("Destroying Ambient Data Framework filter");

        // Clear ambient data context
        AmbientDataContext.setContext(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        if (isRequestPathExcluded(requestPath)) {
            LOG.debug("Request path '{}' is on excluded path list, ADF processing will be skipped.", requestPath);
            chain.doFilter(req, response);
            return;
        }
        LOG.debug("Executing ADF filter for URI:{}, ContextPath:{}", request.getRequestURI(), request.getContextPath());

        // Setting the initialized ADF context again to ensure we have required ADF context
        AmbientDataContext.setContext(ambientDataContext);
        HttpSession session = null;
        if (!adfService) {
            session = request.getSession();
            LOG.debug("Creating new session {}.", session.getId());
        }

        ClaimStore claimStore = null;
        try {
            // Get the claim store from the session or from the request attribute
            claimStore = processClaimStore(session, request);

            Set<URI> formerReadOnlyClaims = new HashSet<>(claimStore.getAllReadOnlyClaims());
            Set<URI> formerImmutableClaims = new HashSet<>(claimStore.getAllImmutableClaims());

            // Make all claims writable
            claimStore.clearReadOnly();
            claimStore.clearImmutable();

            // Make the claim store available in the context
            WebContext.setCurrentClaimStore(claimStore);

            // Put information about the current request in the claim store
            setWebRequestClaims(request, claimStore);

            LOG.debug("Requested by IP: " + ClaimStoreUtil.getClientIpAddressFromClaimStore(claimStore));

            Cookie sessionIdCookie = null;
            Cookie trackingIdCookie = null;
            List<ClaimsCookie> claimsCookies = new ArrayList<>();

            // Find cookies - cookie forwarding enabled.

            LOG.trace("Processing claim cookies.");
            Map<String, Cookie> processedCookies =
                    processClaimsCookies(request, response, claimsCookies, claimStore);
            sessionIdCookie = processedCookies.get(sessionCookieConfig.getCookieName());
            trackingIdCookie = processedCookies.get(trackingCookieConfig.getCookieName());

            // Get session ID. We process session Id & tracking id if it is ADF client or Cartridges installed
            boolean sessionIsNew = false;
            String trackingIdHeader = "";

            if (!adfService || cartridgesInstalled) {
                String sessionId;
                String sessionIdHeader;

                if (sessionIdCookie != null) {
                    sessionIdHeader = sessionIdCookie.getValue();
                    sessionId = httpHeaderProcessor.cleanContent(sessionIdHeader);
                    LOG.debug("SessionId: {}", sessionId);
                    sessionIsNew = false;
                }
                else {
                    // Create new session ID and store it in a temporary cookie
                    sessionId = generateCookieId();
                    sessionIdHeader = httpHeaderProcessor.createValidHttpHeader(sessionId);
                    setCookie(sessionCookieConfig, false, response, sessionIdHeader);
                    LOG.trace("SessionId: {} created", sessionId);
                    sessionIsNew = true;
                }

                // Get tracking ID
                String trackingId;
                if (trackingIdCookie != null) {
                    trackingIdHeader = trackingIdCookie.getValue();
                    trackingId = httpHeaderProcessor.cleanContent(trackingIdHeader);
                    LOG.trace("There is a tracking cookie {} in session: {}", trackingId, sessionId);
                }
                else {
                    // Create new tracking ID
                    trackingId = generateCookieId();
                    trackingIdHeader = httpHeaderProcessor.createValidHttpHeader(trackingId);
                    LOG.trace("There is no tracking cookie in session: {}, so generated a new one: {}",
                            sessionId, trackingId);
                }
                // Put information about the current session in the claim store
                setWebSessionClaims(session, sessionId, trackingId, claimStore);
            }

            setReadOnlyClaims(claimStore, formerReadOnlyClaims);
            setImmutableClaims(claimStore, formerImmutableClaims);

            // Events are handled in case of ADF client or ADF Service with Cartridges installed.
            if (!adfService || cartridgesInstalled) {
                claimStore = processStartEvents(claimStore, sessionIsNew);
                // send tracking cookie if it is not inhibited
                setTrackingCookie(response, claimStore, trackingIdCookie, trackingIdHeader);
            }

            // Process other filters, servlets, JSPs etc.
            chain.doFilter(req, response);

            // Events are handled differently in client and service implementations
            if (!adfService || cartridgesInstalled) {
                claimStore = processEndEvents(claimStore);
            }
            // Check if the session is still active in case it is ADF Client
            if (!adfService) {
                session = request.getSession(false);
                if (session != null) {
                    session.setAttribute(SESSION_KEY_CLAIMSTORE, claimStore);
                    LOG.debug("Setting ClaimStore in session {}", session.getId());
                }
            }
        }
        catch (UnauthorizedException e) {
            LOG.debug("Unauthorized request: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        catch (BadRequestException e) {
            LOG.debug("Bad request {}", e.getMessage());
        }
        catch (AmbientDataException e) {
            LOG.debug("Ambient data exception {}", e.getMessage());
            throw new ServletException("Ambient data exception", e);
        }
        finally {
            // Don't keep a reference to the claim store after returning from this method
            WebContext.setCurrentClaimStore(null);
            // Remove from the ClaimStore the REQUEST claims
            if (claimStore != null) {
                claimStore.removeRequestScopedClaims();
            }
            LOG.debug("Completed executing ADF filter for Request URI:{}", request.getRequestURI());
        }
    }

    private void setTrackingCookie(HttpServletResponse response, ClaimStore claimStore,
                                   Cookie trackingIdCookie, String trackingIdHeader) {
        if (trackingIdCookie == null) {
            LOG.trace("There is no tracking cookie in the request!");
            Boolean cookieClaim = (Boolean) claimStore.get(cookieClaimName);
            LOG.trace("CookieClaim -> {}:{}", cookieClaimName, cookieClaimDefaultValue);
            if (cookieClaim != null && cookieClaim) {
                LOG.trace("CookieClaim has value true, setting tracking cookie!");
                setCookie(trackingCookieConfig, true, response, trackingIdHeader);
            }
        }
    }

    private ClaimStore processClaimStore(HttpSession session, HttpServletRequest request) {
        ClaimStore claimStore;
        if (session != null) {
            LOG.debug("Fetching ClaimStore stored in session: {}", session.getId());
            claimStore = (ClaimStore) session.getAttribute(SESSION_KEY_CLAIMSTORE);
        }
        else {
            LOG.debug("Fetching ClaimStore stored in request");
            claimStore = (ClaimStore) request.getAttribute(REQUEST_KEY_CLAIMSTORE);
        }

        if (claimStore == null) {
            LOG.debug("Creating new ClaimStore");

            // Create a new claim store and store it in the session
            claimStore = claimStoreProvider.newClaimStore();

            if (claimStore == null) {
                // in case the provider gives us a null claim store we use our default Java claimStore.
                LOG.debug("ClaimStoreProvider {} provided a null ClaimStore; using a default created one instead",
                        claimStoreProvider);

                claimStore = new JavaClaimStore();
            }

            // Insert the cookie claim
            claimStore.put(cookieClaimName, cookieClaimDefaultValue);
            LOG.trace("The cookie claim store was inserted {}:{}", cookieClaimName, cookieClaimDefaultValue);

            if (session != null) {
                session.setAttribute(SESSION_KEY_CLAIMSTORE, claimStore);
            }
            else {
                request.setAttribute(REQUEST_KEY_CLAIMSTORE, claimStore);
            }
        }
        else {
            LOG.debug("Returning existing ClaimStore");
            claimStore = ((DefaultClaimStore) claimStore).clone();
        }
        return claimStore;
    }

    private Cookie processAndSetCookie(Cookie cookie, HttpServletResponse response,
                                       CookieConfig cookieConfig, ClaimStore claimStore) {
        if (isCookieValid(cookie)) {
            return cookie;
        }
        else if (cookie.getValue() != null && !httpHeaderProcessor.isValidationActive()) {
            String cookieValue = httpHeaderProcessor.createValidHttpHeader(cookie.getValue());
            cookie.setValue(cookieValue);
            setCookie(cookieConfig, false, response, cookieValue);
            LOG.debug("Digest is added because header validation is in the grace period!");
            return cookie;
        }
        else {
            LOG.warn("Received an invalid cookie ({}) from the IP address: {}",
                    new Object[]{cookie.getName(), ClaimStoreUtil.getClientIpAddressFromClaimStore(claimStore)});
        }
        return null;
    }

    /**
     * Checks if processor is enabled and if the cookie value is valid.
     * If it's not valid but the validator is not active yet, calculates a digest and appends it to the cookie value.
     *
     * @param cookie The cookie to validate.
     * @return true, if processor is disabled, cookie value is valid or validation is not active yet, false otherwise.
     */
    private boolean isCookieValid(Cookie cookie) {
        if (!httpHeaderProcessor.isProcessorEnabled()) {
            return true;
        }
        return cookie.getValue() != null &&
                httpHeaderProcessor.validateHttpHeader(cookie.getValue());
    }

    private void setImmutableClaims(ClaimStore claimStore, Set<URI> formerImmutableClaims) {
        claimStore.setImmutableClaims(formerImmutableClaims);
    }

    private void setReadOnlyClaims(ClaimStore claimStore, Set<URI> formerReadOnlyClaims) {
        List<URI> readOnlyClaims = new ArrayList<>();
        readOnlyClaims.add(WebClaims.SESSION_ID);
        readOnlyClaims.add(WebClaims.TRACKING_ID);
        readOnlyClaims.add(WebClaims.SESSION_ATTRIBUTES);
        readOnlyClaims.add(WebClaims.REQUEST_URI);
        readOnlyClaims.add(WebClaims.REQUEST_FULL_URL);
        readOnlyClaims.add(WebClaims.REQUEST_HEADERS);
        readOnlyClaims.add(WebClaims.REQUEST_PARAMETERS);
        readOnlyClaims.add(WebClaims.SERVER_VARIABLES);
        readOnlyClaims.add(WebClaims.REQUEST_COOKIES);
        readOnlyClaims.addAll(formerReadOnlyClaims);
        claimStore.setReadOnlyClaims(readOnlyClaims);
    }

    private String generateCookieId() {
        return instanceId + UUID.randomUUID();
    }

    private void setCookie(CookieConfig cookieConfig, boolean persistent, HttpServletResponse response, String id) {
        LOG.trace("setCookie -> name: {}, persistent: {}, id:{}", cookieConfig.getCookieName(), persistent, id);
        // NOTE: Unfortunately, the Java EE version that
        // we use does not support the non-standard HttpOnly attribute for cookies.
        // Therefore, we create the Set-Cookie header manually instead of using response.setCookie(...).

        StringBuilder sb = new StringBuilder();

        sb.append(cookieConfig.getCookieName());
        sb.append('=');
        sb.append(id);

        if (persistent) {
            // Add Expires attribute, set it to some far future date
            sb.append("; Expires=Fri, 01-Jan-2100 00:00:00 GMT");
        }
        if (StringUtils.isNotEmpty(cookieConfig.getPath())) {
            sb.append("; path=").append(cookieConfig.getPath());
        }
        else {
            sb.append("; path=").append("/");
        }

        if (getAmbientDataConfig() != null && getAmbientDataConfig().isSecureCookies()) {
            sb.append("; Secure");
        }
        sb.append("; HttpOnly");
        response.addHeader("Set-Cookie", sb.toString());
    }

    private static void setWebSessionClaims(HttpSession session, String sessionId,
                                            String trackingId, ClaimStore claimStore) {
        claimStore.put(WebClaims.SESSION_ID, sessionId);
        claimStore.put(WebClaims.TRACKING_ID, trackingId);

        Map<String, Object> attributes = new HashMap<>();
        if (session != null) {
            for (Enumeration e = session.getAttributeNames(); e.hasMoreElements();) {
                String name = (String) e.nextElement();
                if (!SESSION_KEY_CLAIMSTORE.equals(name)) {
                    // claim store should not contain reference to itself.
                    attributes.put(name, session.getAttribute(name));
                }
            }
        }

        // Session attributes
        claimStore.put(WebClaims.SESSION_ATTRIBUTES, Collections.unmodifiableMap(attributes));
    }

    @SuppressWarnings("deprecation")
    private static void setWebRequestClaims(HttpServletRequest request, ClaimStore claimStore) {
        // Request URI
        claimStore.put(WebClaims.REQUEST_URI, request.getRequestURI());

        // Request full URL
        StringBuffer fullUrl = request.getRequestURL();
        if (fullUrl != null) {
            claimStore.put(WebClaims.REQUEST_FULL_URL, fullUrl.toString());
        }

        Map<String, String[]> headers = new HashMap<>();
        for (Enumeration e1 = request.getHeaderNames(); e1.hasMoreElements();) {
            String name = (String) e1.nextElement();

            List<String> values = new ArrayList<>();
            for (Enumeration e2 = request.getHeaders(name); e2.hasMoreElements();) {
                values.add((String) e2.nextElement());
            }

            // TT 68924: Store headers with lower-case names
            headers.put(name.toLowerCase(), values.toArray(EMPTY));
        }

        // Request headers
        claimStore.put(WebClaims.REQUEST_HEADERS, Collections.unmodifiableMap(headers));

        Map<String, String[]> parameters = new HashMap<>();
        parameters.putAll(request.getParameterMap());
        parameters.put(Constants.CONTENT_LENGTH, new String[]{String.valueOf(request.getContentLength())});
        parameters.put(Constants.CONTENT_TYPE, new String[]{request.getContentType()});
        parameters.put(Constants.QUERY_STRING, new String[]{request.getQueryString()});
        parameters.put(Constants.PATH_INFO, new String[]{resolveRequestPathInfo(request)});

        // Request parameters
        claimStore.put(WebClaims.REQUEST_PARAMETERS, Collections.unmodifiableMap(parameters));

        Map<String, String> variables = new HashMap<>(INITIAL_CAPACITY);
        variables.put(Constants.AUTH_TYPE, request.getAuthType());
        variables.put(Constants.DOCUMENT_ROOT, request.getServletContext().getRealPath("/"));
        variables.put(Constants.PATH_TRANSLATED, request.getPathTranslated());
        variables.put(Constants.REMOTE_ADDR, request.getRemoteAddr());
        variables.put(Constants.REMOTE_HOST, request.getRemoteHost());
        variables.put(Constants.REMOTE_USER, request.getRemoteUser());
        variables.put(Constants.REQUEST_METHOD, request.getMethod());
        variables.put(Constants.SECURE, Boolean.toString(request.isSecure()));
        variables.put(Constants.SCRIPT_NAME, request.getServletPath());
        variables.put(Constants.SERVER_NAME, request.getServerName());
        variables.put(Constants.SERVER_PORT, String.valueOf(request.getServerPort()));
        variables.put(Constants.SERVER_PROTOCOL, request.getProtocol());

        // Server variables
        claimStore.put(WebClaims.SERVER_VARIABLES, Collections.unmodifiableMap(variables));

        Map<String, String> cookieValues = new HashMap<>();
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                cookieValues.put(cookie.getName(), cookie.getValue());
            }
        }
        claimStore.put(WebClaims.REQUEST_COOKIES, Collections.unmodifiableMap(cookieValues));
    }

    /**
     * Add the list of claims cookies to claim store.
     *
     * @param claimsCookies List of claims cookies.
     * @param claimStore    Claim store
     */
    private Map<String, Cookie> processClaimsCookies(HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     List<ClaimsCookie> claimsCookies,
                                                     ClaimStore claimStore)
            throws UnsupportedEncodingException {
        LOG.trace("Processing cookie claims.");
        Map<String, Cookie> processedCookieMap = new HashMap<>();

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                String name = cookie.getName();
                LOG.trace("Checking if the cookie name: {} matches configured session cookies {} " +
                        "or tracking cookie {} or adfCookiePrefix {}.", name, sessionCookieConfig.getCookieName(),
                        trackingCookieConfig.getCookieName(), adfCookiePrefix);
                if (sessionCookieConfig.getCookieName().equals(name)) {
                    processedCookieMap.put(sessionCookieConfig.getCookieName(),
                            processAndSetCookie(cookie, response, sessionCookieConfig, claimStore));
                }
                else if (trackingCookieConfig.getCookieName().equals(name)) {
                    processedCookieMap.put(trackingCookieConfig.getCookieName(),
                            processAndSetCookie(cookie, response, trackingCookieConfig, claimStore));
                }
                else if (name.startsWith(adfCookiePrefix)) {
                    claimsCookies.add(new ClaimsCookie(name, cookie.getValue().getBytes("UTF-8")));
                }
                else {
                    LOG.debug("Cookie {} has no match in Ambient Data Framework configuration.", name);
                }
            }
        }

        LOG.debug("Cookie forwarding is enabled: {}", isADFCookieEnabled);
        if (isADFCookieEnabled && isCookieForwardingAllowed(claimStore)) {
            addGloballyAcceptedClaimsToClaimstore(claimsCookies, claimStore);
        }
        return processedCookieMap;
    }

    /**
     * Determining if the cookie forwarding condition is fulfilled.
     *
     * @param claimStore Claim store.
     * @return true if the cookie forwarding is allowed otherwise returns false.
     */
    private boolean isCookieForwardingAllowed(ClaimStore claimStore) {
        boolean whiteListCondition = (whiteListFilter != null && whiteListFilter.isValid(claimStore));
        LOG.debug("IP address is in the white list: {}", whiteListCondition);
        LOG.debug("Cookie forwarding for current request is allowed: {}", whiteListCondition);
        return whiteListCondition;
    }

    /**
     * Adding  globally accepted claims to claim store.
     *
     * @param claimsCookies List of claims cookies.
     * @param claimStore    Claim store.
     */
    private void addGloballyAcceptedClaimsToClaimstore(List<ClaimsCookie> claimsCookies, ClaimStore claimStore) {
        LOG.trace("Deserializing claim cookies.");
        Map<URI, Object> extractedCookieClaims = ClaimCookieDeserializer.deserializeClaims(claimsCookies);
        for (URI claimUri : extractedCookieClaims.keySet()) {
            LOG.trace("Checking if claim {} is on a globally accepted claims list.", claimUri);
            if (globallyAcceptedClaims.contains(claimUri.toString())) {
                claimStore.put(claimUri, extractedCookieClaims.get(claimUri), ClaimType.IMMUTABLE);
                LOG.trace("Added globally accepted claim: {} to claimstore.", claimUri);
            }
            else {
                LOG.debug("Claim: {} is not on the globally accepted claims list.", claimUri);
            }
        }
    }

    private boolean getBooleanFilterParameter(FilterConfig filterConfig, String parameterName) {
        if (filterConfig != null) {
            String value = filterConfig.getInitParameter(parameterName);
            if (value != null) {
                return Boolean.parseBoolean(value);
            }
        }
        return false;
    }

    private static String resolveRequestPathInfo(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (StringUtils.isEmpty(pathInfo)) {
            return request.getRequestURI().substring(request.getContextPath().length());
        }
        return pathInfo;
    }

    protected abstract void initializeEngine() throws AmbientDataException;

    protected abstract ClaimStore processStartEvents(ClaimStore claimStore, boolean sessionIsNew)
            throws AmbientDataException;

    protected abstract ClaimStore processEndEvents(ClaimStore claimStore) throws AmbientDataException;

    public void setADFService(boolean adfServiceValue) {
        this.adfService = adfServiceValue;
        LOG.debug("ADF service set to: {}", adfServiceValue);
    }
}

/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.core.util;

/**
 * dd4t-2
 */

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for Http related things.
 */
public final class HttpUtils {

    private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);

    private HttpUtils () {
    }

    public static ServletContext getCurrentServletContext () {
        return getCurrentRequest().getServletContext();
    }

    public static String getOriginalUri (final HttpServletRequest request) {
        String orgUri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);

        if (StringUtils.isNotEmpty(orgUri)) {
            return orgUri;
        } else {
            return request.getRequestURI();
        }
    }

    public static String getCurrentURL (final HttpServletRequest request) {
        String url;

        DispatcherType dispatcherType = request.getDispatcherType();
        if (dispatcherType == DispatcherType.ERROR) {
            url = request.getRequestURI();
        } else if (dispatcherType == DispatcherType.INCLUDE) {
            url = (String) request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI);
        } else {
            url = getOriginalUri(request);
        }

        return url;
    }

    public static String getOriginalFullUrl (final HttpServletRequest request) {
        return getFullUrl(request, getOriginalUri(request));
    }

    public static String getFullUrl (final HttpServletRequest request, final String location) {
        String contextPath = "/".equals(request.getContextPath()) ? "" : request.getContextPath();
        return String.format("%s://%s:%d%s%s", request.getScheme(), request.getServerName(), request.getServerPort(), contextPath, location);
    }

    public static HttpServletRequest getCurrentRequest () {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    public static String createPathFromUri (String uri, int level) {
        StringBuilder searchPath = new StringBuilder("");
        String[] paths = uri.split("/");

        int count = 0;
        for (int i = 0; i < paths.length && count < level; i++) {
            String path = paths[i];
            if (StringUtils.isEmpty(path) || i == paths.length - 1 && path.indexOf('.') >= 0) {
                continue;
            }
            searchPath.append("/");
            searchPath.append(path);
            count++;
        }

        return searchPath.length() == 0 ? "/" : searchPath.toString();
    }

    /**
     * Returns the most likely IP-address of the client, no guarantee though.
     * <p/>
     * If the client is behind a proxy it should be the 1st ip address in the
     * HTTP_X_FORWARDED_FOR header if not we use the REMOTE_ADDR header
     */
    private static String getClientIP (final HttpServletRequest request) {
        String clientIP;

        String s = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (null != s) {
            clientIP = s.split(",")[0];
        } else {
            clientIP = request.getRemoteAddr();
        }
        return clientIP;
    }

    public static boolean isLocalDomainRequest (final HttpServletRequest request) throws UnknownHostException {
        return isLocalDomainAddress(getClientIP(request));
    }

    /**
     * Checking for local ip addresses, e.g.
     * <p/>
     * <pre>
     *     10.x.x.x
     *     172.[16-31].x.x
     *     192.168.x.x
     *     127.0.0.1
     * </pre>
     */
    private static boolean isLocalDomainAddress (final String ipAddress) throws UnknownHostException {
        final InetAddress inetAddress = InetAddress.getByName(ipAddress);
        return inetAddress.isAnyLocalAddress() || inetAddress.isLinkLocalAddress() || inetAddress.isMulticastAddress() || inetAddress.isSiteLocalAddress();
    }

    /**
     * Returns the content type string to be used in the response based on the
     * extension of an url. Currently only TXT, HTML and XML are supported. If
     * no match is found, will return the default text/html
     *
     * @param url , the url used to detect the content type or mime type
     * @return the content type, e.g. text/plain, text/html or text/xml
     */
    public static String getContentTypeByExtension (final String url) {
        String extension = url.substring(url.lastIndexOf('.') + 1);

        if ("txt".equalsIgnoreCase(extension)) {
            return "text/plain";
        }

        if ("xml".equalsIgnoreCase(extension)) {
            return "text/xml";
        }

        // Default content type
        return "text/html";
    }

    public static void appendAttribute (StringBuilder sb, String name, String value) {
        appendAttribute(sb, name, value, true);
    }

    public static void appendAttribute (StringBuilder sb, String name, String value, boolean suppressIfEmpty) {
        String attributeValue = "";
        if (value != null) {
            attributeValue = value;
        }

        if (!suppressIfEmpty || attributeValue.length() > 0) {
            sb.append(' ').append(name).append("=\"").append(attributeValue).append('"');
        }
    }

    /**
     * Parse a query string (e.g. <code>"abc=123&cde=456"</code>) into a list of name/value pairs.
     */
    public static List<NameValuePair> parseQueryParams (final String queryString) {

        if (StringUtils.isEmpty(queryString)) {
            return Collections.emptyList();
        }

        List<NameValuePair> result = new ArrayList<NameValuePair>();

        for (String param : queryString.split("&")) {
            String[] paramParts = param.split("=");
            String name = paramParts[0];
            String value = paramParts.length == 2 ? paramParts[1] : "";

            result.add(new BasicNameValuePair(name, value));
        }

        return result;
    }

    public static URI parseUri (String uriStr) {
        if (StringUtils.isEmpty(uriStr)) {
            return null;
        }

        try {
            return new URI(uriStr);
        } catch (URISyntaxException e) {
            LOG.warn("Could not parse URI: {}", uriStr, e);
            return null;
        }
    }

    public static Cookie findCookieByName (String name) {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }

        return null;
    }

    public static String setSchemaForUrl (String input, String prepend) {
        if (StringUtils.isEmpty(input)) {
            return "";
        }

        if (StringUtils.isEmpty(prepend)) {
            prepend = "http://";
        }

        if (!input.matches("^[\\w]+://.*")) {
            return String.format("%s%s", prepend, input);
        }
        return input;
    }

    public static String appendDefaultPageIfRequired (String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        String ext = FilenameUtils.getExtension(url);

        if (StringUtils.isEmpty(ext)) {
            if (url.endsWith("/")) {
                url += Constants.DEFAULT_PAGE;
            } else {
                url += "/" + Constants.DEFAULT_PAGE;
            }
        }

        return url;
    }

    public static String normalizeUrl (String url) {
        return url == null ? null : url.replaceAll("//+", "/");
    }


    public static Locale buildLocale (String url) {
        Locale pageLocale = null;
        try {
            Path path = Paths.get(url);
            if (path != null) {
                String[] pathInfo = path.subpath(0, 1).toString().split("_", 2);
                if (pathInfo.length == 2) {
                    pageLocale = new Locale(pathInfo[0], pathInfo[1]);
                }
            }
        } catch (InvalidPathException ipe) {
            LOG.error("Could not parse the path: " + url, ipe);
        }
        return pageLocale;
    }

    public static String removeNonAlphaNumeric (String key) {
        return replaceNonAlphaNumeric(key, "");
    }

    public static String replaceNonAlphaNumeric (String key, String replacement) {
        if (key == null) {
            return "";
        }

        if (replacement == null) {
            replacement = "";
        }

        return key.replaceAll("\\W+", replacement);
    }

    /*
            Looks up the Preview Session token from the cookie in the request
            */
    public static String getSessionPreviewToken (HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (TridionUtils.PREVIEW_SESSION_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}

package org.dd4t.core.util;

import org.dd4t.core.request.RequestContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public class TridionUtils {

    public static final String TCM_REGEX = "(tcm:[0-9]+-[0-9]+(-[0-9]+)?)";
    public static final String PREVIEW_SESSION_TOKEN = "preview-session-token";

    public static int extractPublicationIdFromTcmUri(String tcmUri) throws ParseException {
        TCMURI fullTcmUri = new TCMURI(tcmUri);
        return fullTcmUri.getItemId();
    }

    public static String constructFullTcmPublicationUri(int id) {
        return constructFullTcmPublicationUri(String.valueOf(id));
    }

    public static String constructFullTcmPublicationUri(String id) {
        return String.format("tcm:0-%s-1",id);
    }

    /*
    Looks up the Preview Session token from the cookie in the request
    */
    public static String getSessionPreviewToken() {
        return getSessionPreviewToken(getCurrentRequest());
    }

    /*
    Looks up the Preview Session token from the cookie in the request
     */
    public static String getSessionPreviewToken(RequestContext context) {
        if (context == null) {
            return null;
        }

        return getSessionPreviewToken(context.getServletRequest());
    }

    /*
    Looks up the Preview Session token from the cookie in the request
     */
    public static String getSessionPreviewToken(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (PREVIEW_SESSION_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    public static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}

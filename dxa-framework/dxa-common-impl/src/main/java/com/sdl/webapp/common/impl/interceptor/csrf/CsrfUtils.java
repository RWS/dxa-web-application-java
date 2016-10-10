package com.sdl.webapp.common.impl.interceptor.csrf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Provides CSRF tokens for request and methods to work with them.
 */
public final class CsrfUtils {

    public static final String CSRF_TOKEN_NAME = "_CSRF_TOKEN_DXA_";

    private CsrfUtils() {
    }

    private static String getToken() {
        return UUID.randomUUID().toString();
    }

    public static String setToken(HttpSession session) {
        if (session.getAttribute(CSRF_TOKEN_NAME) != null) {
            return session.getAttribute(CSRF_TOKEN_NAME).toString();
        }

        synchronized (CsrfUtils.class) {
            String token = getToken();
            session.setAttribute(CSRF_TOKEN_NAME, token);
            return token;
        }
    }

    static boolean verifyToken(HttpServletRequest request) {
        String csrfToken = request.getParameter(CSRF_TOKEN_NAME);
        return !"POST".equals(request.getMethod()) || (csrfToken != null && csrfToken.equals(request.getSession().getAttribute(CSRF_TOKEN_NAME)));
    }
}

package com.sdl.webapp.common.impl.interceptor.csrf;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CsrfInterceptorTest {

    @Test
    public void shouldFailIfTokenIsWrong() throws Exception {
        //given 
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setParameter(CsrfUtils.CSRF_TOKEN_NAME, "test");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(CsrfUtils.CSRF_TOKEN_NAME, "test2");
        request.setSession(session);

        MockHttpServletResponse response = new MockHttpServletResponse();

        //when
        boolean isFalse = new CsrfInterceptor().preHandle(request, response, null);

        //then
        assertFalse(isFalse);
        assertEquals(SC_FORBIDDEN, response.getStatus());
    }

    @Test
    public void shouldAllowRequestIfGetRequest() throws Exception {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");

        MockHttpServletResponse response = new MockHttpServletResponse();

        //when
        boolean isTrue = new CsrfInterceptor().preHandle(request, response, null);

        //then
        assertTrue(isTrue);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldAllowRequestIfTokenIsGood() throws Exception {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setParameter(CsrfUtils.CSRF_TOKEN_NAME, "test");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(CsrfUtils.CSRF_TOKEN_NAME, "test");
        request.setSession(session);

        MockHttpServletResponse response = new MockHttpServletResponse();

        //when
        boolean isTrue = new CsrfInterceptor().preHandle(request, response, null);

        //then
        assertTrue(isTrue);
        assertEquals(200, response.getStatus());
    }
}
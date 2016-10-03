package com.sdl.webapp.common.impl.interceptor.csrf;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CsrfUtilsTest {

    @Test
    public void shouldVerifyTokenOnlyIfPOSTRequest() {
        //given 
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");

        //when
        boolean isTrue = CsrfUtils.verifyToken(request);

        //then
        assertTrue(isTrue);
    }

    @Test
    public void shouldVerifyTokenWithSession() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(CsrfUtils.CSRF_TOKEN_NAME, "test");
        request.setSession(session);
        request.setParameter(CsrfUtils.CSRF_TOKEN_NAME, "test");

        //when
        boolean isTrue = CsrfUtils.verifyToken(request);

        //then
        assertTrue(isTrue);
    }

    @Test
    public void shouldVerifyTokenWithSessionAndSayIfWrong() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(CsrfUtils.CSRF_TOKEN_NAME, "test");
        request.setSession(session);
        request.setParameter(CsrfUtils.CSRF_TOKEN_NAME, "test1");

        //when
        boolean isFalse = CsrfUtils.verifyToken(request);

        //then
        assertFalse(isFalse);
    }

    @Test
    public void shouldSetSessionTokenToSession() {
        //given
        MockHttpSession session = new MockHttpSession();

        //when
        String token = CsrfUtils.setToken(session);

        //then
        assertNotNull(token);
    }

    @Test
    public void shouldReturnSameTokenIfAlreadySet() {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(CsrfUtils.CSRF_TOKEN_NAME, "test");

        //when
        String token = CsrfUtils.setToken(session);

        //then
        assertEquals("test", token);
        assertEquals("test", session.getAttribute(CsrfUtils.CSRF_TOKEN_NAME));
    }
}
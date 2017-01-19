package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationNotFoundException;
import com.sdl.webapp.common.api.localization.LocalizationNotResolvedException;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import com.sdl.webapp.common.api.localization.LocalizationResolverException;
import com.sdl.webapp.common.api.localization.UnknownLocalizationHandler;
import com.sdl.webapp.common.impl.localization.LocalizationImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebRequestContextImplTest {

    @InjectMocks
    private WebRequestContext webRequestContext = spy(new WebRequestContextImpl());

    @Mock
    private LocalizationResolver localizationResolver;

    @Mock
    private HttpServletRequest servletRequest;

    @Before
    public void init() {
        when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/context/publication/request"));
        when(servletRequest.getRequestURI()).thenReturn("/context/publication/request");
        when(servletRequest.getContextPath()).thenReturn("/context");
    }


    @Test(expected = LocalizationNotFoundException.class)
    public void shouldThrowExceptionIfNoLocalizationFoundWithSpecialHandler() throws Exception {
        //given
        when(localizationResolver.getLocalization(anyString())).thenThrow(new LocalizationResolverException("Test"));

        UnknownLocalizationHandler unknownLocalizationHandler = mock(UnknownLocalizationHandler.class);
        when(unknownLocalizationHandler.handleUnknown(any(Exception.class), any(ServletRequest.class)))
                .thenReturn(null);
        ReflectionTestUtils.setField(webRequestContext, "unknownLocalizationHandler", unknownLocalizationHandler);

        //when
        webRequestContext.getLocalization();

        //then
        // exception
    }

    @Test
    public void shouldResolveLocalizationWithSpecialHandler() throws Exception {
        //given
        when(localizationResolver.getLocalization(anyString())).thenThrow(new LocalizationResolverException("Test"));

        UnknownLocalizationHandler unknownLocalizationHandler = mock(UnknownLocalizationHandler.class);
        LocalizationImpl localization = mock(LocalizationImpl.class);
        when(unknownLocalizationHandler.handleUnknown(any(Exception.class), any(ServletRequest.class))).thenReturn(localization);
        ReflectionTestUtils.setField(webRequestContext, "unknownLocalizationHandler", unknownLocalizationHandler);

        //when
        Localization result = webRequestContext.getLocalization();

        //then
        assertSame(localization, result);
        verify(unknownLocalizationHandler).handleUnknown(any(Exception.class), any(ServletRequest.class));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void shouldFallBackExceptionIfDefaultAndCustomLocalizationResolversFailed() throws LocalizationResolverException {
        //given
        when(localizationResolver.getLocalization(anyString())).thenThrow(new LocalizationResolverException("Test"));

        UnknownLocalizationHandler unknownLocalizationHandler = mock(UnknownLocalizationHandler.class);
        when(unknownLocalizationHandler.handleUnknown(any(Exception.class), any(ServletRequest.class))).thenReturn(null);
        LocalizationNotResolvedException exception = new LocalizationNotResolvedException("Test exception");
        when(unknownLocalizationHandler.getFallbackException(any(Exception.class), any(ServletRequest.class))).thenReturn(exception);
        ReflectionTestUtils.setField(webRequestContext, "unknownLocalizationHandler", unknownLocalizationHandler);

        //when
        try {
            webRequestContext.getLocalization();
        } catch (LocalizationNotResolvedException e) {
            //then
            assertEquals("Test exception", e.getMessage());
        }

        //then
        verify(unknownLocalizationHandler).handleUnknown(any(Exception.class), any(ServletRequest.class));
        verify(unknownLocalizationHandler).getFallbackException(any(Exception.class), any(ServletRequest.class));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void shouldFallBackToDefaultExceptionIfDefaultAndCustomLocalizationResolversFailedAndNoFallback() throws LocalizationResolverException {
        //given
        when(localizationResolver.getLocalization(anyString())).thenThrow(new LocalizationResolverException("Test"));

        UnknownLocalizationHandler unknownLocalizationHandler = mock(UnknownLocalizationHandler.class);
        when(unknownLocalizationHandler.handleUnknown(any(Exception.class), any(ServletRequest.class))).thenReturn(null);
        when(unknownLocalizationHandler.getFallbackException(any(Exception.class), any(ServletRequest.class))).thenReturn(null);
        ReflectionTestUtils.setField(webRequestContext, "unknownLocalizationHandler", unknownLocalizationHandler);

        //when
        try {
            webRequestContext.getLocalization();
        } catch (LocalizationNotFoundException e) {
            //then
            assertNotEquals("Test exception", e.getMessage());
        }

        //then
        verify(unknownLocalizationHandler).handleUnknown(any(Exception.class), any(ServletRequest.class));
        verify(unknownLocalizationHandler).getFallbackException(any(Exception.class), any(ServletRequest.class));
    }

    @Test(expected = LocalizationNotFoundException.class)
    public void shouldThrowExceptionIfNoLocalizationFoundWithoutSpecialHandler() throws Exception {
        //given
        when(localizationResolver.getLocalization(anyString())).thenThrow(new LocalizationResolverException("Test"));
        //no UnknownLocalizationHandler set

        //when
        webRequestContext.getLocalization();

        //then
        // exception
    }

    @Test
    public void shouldReturnContextPath() {
        //when
        String contextPath = webRequestContext.getContextPath();

        //then
        assertEquals("/context", contextPath);
    }

    @Test
    public void shouldReturnRequestPath_RelativeToContext() {
        //when
        String requestPath = webRequestContext.getRequestPath();

        //then
        assertEquals("/publication/request", requestPath);
    }

    @Test
    public void shouldReturnBaseUrl() {
        //when
        String baseUrl = webRequestContext.getBaseUrl();

        //then
        assertEquals("http://localhost:8080", baseUrl);
    }

    @Test
    public void shouldReturnFullUrl() {
        //when
        String fullUrl = webRequestContext.getFullUrl();

        //then
        assertEquals("http://localhost:8080/context/publication/request", fullUrl);
    }

    @Test
    public void shouldBeAwareOfRootRequests() {
        //given
        when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/"));
        when(servletRequest.getRequestURI()).thenReturn("/");

        //when
        String baseUrl = webRequestContext.getBaseUrl();

        //then
        assertEquals("http://localhost:8080", baseUrl);
    }
}
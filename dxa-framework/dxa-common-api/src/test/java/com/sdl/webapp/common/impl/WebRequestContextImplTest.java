package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationNotFoundException;
import com.sdl.webapp.common.api.localization.LocalizationNotResolvedException;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import com.sdl.webapp.common.api.localization.LocalizationResolverException;
import com.sdl.webapp.common.api.localization.UnknownLocalizationHandler;
import com.sdl.webapp.common.impl.localization.LocalizationImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebRequestContextImplTest {

    @InjectMocks
    private WebRequestContext webRequestContext = spy(new WebRequestContextImpl());

    @Mock
    private LocalizationResolver localizationResolver;

    @Mock
    private HttpServletRequest servletRequest;

    @BeforeEach
    public void init() {
        lenient().when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/context/publication/request"));
        lenient().when(servletRequest.getRequestURI()).thenReturn("/context/publication/request");
        lenient().when(servletRequest.getContextPath()).thenReturn("/context");
    }


    @Test
    public void shouldThrowExceptionIfNoLocalizationFoundWithSpecialHandler() throws Exception {
        Assertions.assertThrows(LocalizationNotFoundException.class, () -> {
            //given
            lenient().when(localizationResolver.getLocalization(anyString())).thenThrow(new LocalizationResolverException("Test"));

            UnknownLocalizationHandler unknownLocalizationHandler = mock(UnknownLocalizationHandler.class);
            lenient().when(unknownLocalizationHandler.handleUnknown(any(Exception.class), any(ServletRequest.class)))
                    .thenReturn(null);
            ReflectionTestUtils.setField(webRequestContext, "unknownLocalizationHandler", unknownLocalizationHandler);

            //when
            webRequestContext.getLocalization();

            //then
            // exception
        });
    }

    @Test
    public void shouldResolveLocalizationWithSpecialHandler() throws Exception {
        //given
        lenient().when(localizationResolver.getLocalization(anyString())).thenThrow(new LocalizationResolverException("Test"));

        UnknownLocalizationHandler unknownLocalizationHandler = mock(UnknownLocalizationHandler.class);
        LocalizationImpl localization = mock(LocalizationImpl.class);
        lenient().when(unknownLocalizationHandler.handleUnknown(any(Exception.class), any(ServletRequest.class))).thenReturn(localization);
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
        lenient().when(localizationResolver.getLocalization(anyString())).thenThrow(new LocalizationResolverException("Test"));

        UnknownLocalizationHandler unknownLocalizationHandler = mock(UnknownLocalizationHandler.class);
        lenient().when(unknownLocalizationHandler.handleUnknown(any(Exception.class), any(ServletRequest.class))).thenReturn(null);
        LocalizationNotResolvedException exception = new LocalizationNotResolvedException("Test exception");
        lenient().when(unknownLocalizationHandler.getFallbackException(any(Exception.class), any(ServletRequest.class))).thenReturn(exception);
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
        lenient().when(localizationResolver.getLocalization(anyString())).thenThrow(new LocalizationResolverException("Test"));

        UnknownLocalizationHandler unknownLocalizationHandler = mock(UnknownLocalizationHandler.class);
        lenient().when(unknownLocalizationHandler.handleUnknown(any(Exception.class), any(ServletRequest.class))).thenReturn(null);
        lenient().when(unknownLocalizationHandler.getFallbackException(any(Exception.class), any(ServletRequest.class))).thenReturn(null);
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

    @Test
    public void shouldThrowExceptionIfNoLocalizationFoundWithoutSpecialHandler() throws Exception {
        Assertions.assertThrows(LocalizationNotFoundException.class, () -> {
            // Given
            lenient().when(localizationResolver.getLocalization(anyString())).thenThrow(new LocalizationResolverException("Test"));
            // No UnknownLocalizationHandler set

            // When
            webRequestContext.getLocalization();

            // Then exception
        });
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
        lenient().when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/"));
        lenient().when(servletRequest.getRequestURI()).thenReturn("/");

        //when
        String baseUrl = webRequestContext.getBaseUrl();

        //then
        assertEquals("http://localhost:8080", baseUrl);
    }
}
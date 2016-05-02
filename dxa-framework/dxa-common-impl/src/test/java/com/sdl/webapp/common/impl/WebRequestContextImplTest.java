package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationNotFoundException;
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

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebRequestContextImplTest {

    @InjectMocks
    private WebRequestContext webRequestContext = spy(new WebRequestContextImpl());

    @Mock
    private LocalizationResolver localizationResolver;

    @Before
    public void init() {
        doReturn("Hello").when(webRequestContext).getFullUrl();
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
}
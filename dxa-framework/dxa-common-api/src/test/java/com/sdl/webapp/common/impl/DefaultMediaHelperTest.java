package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.ScreenWidth;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.sdl.webapp.common.api.contextengine.ContextEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;

import static com.sdl.webapp.common.impl.DefaultMediaHelper.roundWidth;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DefaultMediaHelperTest.MediaHelperTestConfig.class)
@ActiveProfiles("test")
public class DefaultMediaHelperTest {

    @Autowired
    private MediaHelper mediaHelper;

    @Autowired
    private WebRequestContext webRequestContext;

    @Test
    public void testGetResponsiveWidthAbsoluteWidthFactor() {
        when(webRequestContext.getPixelRatio()).thenReturn(1.0);
        assertThat(mediaHelper.getResponsiveWidth("50", 12), is(50));

        when(webRequestContext.getPixelRatio()).thenReturn(1.5);
        assertThat(mediaHelper.getResponsiveWidth("50", 12), is(75));
    }

    @Test
    public void testGetResponsiveWidthExtraSmall() {
        when(webRequestContext.getDisplayWidth()).thenReturn(mediaHelper.getSmallScreenBreakpoint() - 1);
        when(webRequestContext.getMaxMediaWidth()).thenReturn(1024);
        assertThat(mediaHelper.getResponsiveWidth("100%", 3), is(1024));
    }

    @Test
    public void testGetResponsiveWidthSmall() {
        when(webRequestContext.getDisplayWidth()).thenReturn(mediaHelper.getMediumScreenBreakpoint() - 1);
        when(webRequestContext.getMaxMediaWidth()).thenReturn(1024);
        assertThat(mediaHelper.getResponsiveWidth("100%", 3), is(482));
    }

    @Test
    public void testGetResponsiveWidthMedium() {
        when(webRequestContext.getDisplayWidth()).thenReturn(mediaHelper.getLargeScreenBreakpoint() - 1);
        when(webRequestContext.getMaxMediaWidth()).thenReturn(1024);
        assertThat(mediaHelper.getResponsiveWidth("100%", 3), is(166));
    }

    @Test
    public void testGetResponsiveImageUrl() throws Exception {
        //given
        when(webRequestContext.getDisplayWidth()).thenReturn(1920);
        when(webRequestContext.getPixelRatio()).thenReturn(1.0);
        when(webRequestContext.getMaxMediaWidth()).thenReturn(2048);

        //when
        //then
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "100%", 3.3, 12), is("/example_w2048_h621_n.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "100%", 3.3, 6), is("/example_w1024_h311_n.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "100%", 2.0, 12), is("/example_w2048_h1024_n.jpg"));

        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "50%", 3.3, 12), is("/example_w1024_h311_n.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "50%", 3.3, 6), is("/example_w640_h194_n.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "75%", 2.0, 12), is("/example_w2048_h1024_n.jpg"));

        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "320", 2.5, 12), is("/example_w320_h128_n.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "321", 2.5, 12), is("/example_w640_h256_n.jpg"));

        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "639", 2.5, 12), is("/example_w640_h256_n.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "640", 2.5, 12), is("/example_w640_h256_n.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "641", 2.5, 12), is("/example_w1024_h410_n.jpg"));
    }

    @Test
    public void shouldCorrectlyGiveBreakPointsForWidths() {
        assertEquals(roundWidth(100), 160);
        assertEquals(roundWidth(160), 160);
        assertEquals(roundWidth(200), 320);
        assertEquals(roundWidth(320), 320);
        assertEquals(roundWidth(400), 640);
        assertEquals(roundWidth(640), 640);
        assertEquals(roundWidth(800), 1024);
        assertEquals(roundWidth(1024), 1024);
        assertEquals(roundWidth(1600), 2048);
        assertEquals(roundWidth(2048), 2048);
        assertEquals(roundWidth(3000), 2048);
        assertEquals(roundWidth(4000), 2048);
    }


    @Test
    public void shouldReturnTheCorrectScreenSizeByBreakPoint() {
        //when
        when(webRequestContext.getDisplayWidth()).thenReturn(400);
        //then
        assertEquals(ScreenWidth.EXTRA_SMALL, mediaHelper.getScreenWidth());

        //when
        when(webRequestContext.getDisplayWidth()).thenReturn(480);
        //then
        assertEquals(ScreenWidth.SMALL, mediaHelper.getScreenWidth());

        //when
        when(webRequestContext.getDisplayWidth()).thenReturn(900);
        //then
        assertEquals(ScreenWidth.SMALL, mediaHelper.getScreenWidth());

        //when
        when(webRequestContext.getDisplayWidth()).thenReturn(940);
        //then
        assertEquals(ScreenWidth.MEDIUM, mediaHelper.getScreenWidth());

        //when
        when(webRequestContext.getDisplayWidth()).thenReturn(1100);
        //then
        assertEquals(ScreenWidth.MEDIUM, mediaHelper.getScreenWidth());

        //when
        when(webRequestContext.getDisplayWidth()).thenReturn(1140);
        //then
        assertEquals(ScreenWidth.LARGE, mediaHelper.getScreenWidth());

        //when
        when(webRequestContext.getDisplayWidth()).thenReturn(1280);
        //then
        assertEquals(ScreenWidth.LARGE, mediaHelper.getScreenWidth());
    }

    @Configuration
    @Profile("test")
    public static class MediaHelperTestConfig {

        @Bean
        public MediaHelper mediaHelper() {
            return new DefaultMediaHelper();
        }

        @Bean
        public MediaHelper.ResponsiveMediaUrlBuilder responsiveMediaUrlBuilder() {
            return new DefaultMediaHelper.DefaultResponsiveMediaUrlBuilder();
        }

        @Bean
        public WebRequestContext webRequestContext() {
            return mock(WebRequestContext.class);
        }

        @Bean
        public ContextClaimsProvider contextClaimsProvider() {
            return mock(ContextClaimsProvider.class);
        }

        @Bean
        public ContextEngine contextEngine() {
            return mock(ContextEngine.class);
        }

        @Bean
        public HttpServletRequest servletRequest() {
            return new MockHttpServletRequest();
        }

    }
}

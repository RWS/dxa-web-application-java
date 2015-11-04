package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.MediaHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link GenericMediaHelper}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AbstractMediaHelperTestConfig.class)
public class DefaultMediaHelperTest {

    @Autowired
    private MediaHelper mediaHelper;

    @Autowired
    private MockWebRequestContext webRequestContext;

    @Test
    public void testGetResponsiveWidthAbsoluteWidthFactor() {
        webRequestContext.setPixelRatio(1.0);
        assertThat(mediaHelper.getResponsiveWidth("50", 12), is(50));

        webRequestContext.setPixelRatio(1.5);
        assertThat(mediaHelper.getResponsiveWidth("50", 12), is(75));
    }

    @Test
    public void testGetResponsiveWidthExtraSmall() {
        webRequestContext.setDisplayWidth(mediaHelper.getSmallScreenBreakpoint() - 1);
        webRequestContext.setMaxMediaWidth(1024);
        assertThat(mediaHelper.getResponsiveWidth("100%", 3), is(1024));
    }

    @Test
    public void testGetResponsiveWidthSmall() {
        webRequestContext.setDisplayWidth(mediaHelper.getMediumScreenBreakpoint() - 1);
        webRequestContext.setMaxMediaWidth(1024);
        assertThat(mediaHelper.getResponsiveWidth("100%", 3), is(482));
    }

    @Test
    public void testGetResponsiveWidthMedium() {
        webRequestContext.setDisplayWidth(mediaHelper.getLargeScreenBreakpoint() - 1);
        webRequestContext.setMaxMediaWidth(1024);
        assertThat(mediaHelper.getResponsiveWidth("100%", 3), is(166));
    }

    @Test
    public void testGetResponsiveImageUrl() throws Exception {
        //given
        webRequestContext.setDisplayWidth(1920);
        webRequestContext.setPixelRatio(1.0);
        webRequestContext.setMaxMediaWidth(2048);

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
}

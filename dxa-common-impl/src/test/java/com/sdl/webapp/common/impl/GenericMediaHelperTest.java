package com.sdl.webapp.common.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Unit tests for {@code DefaultMediaHelper}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DefaultMediaHelperTestConfig.class)
public class GenericMediaHelperTest {

    @Autowired
    private GenericMediaHelper mediaHelper;

    @Test
    public void testGetResponsiveImageUrl() throws Exception {
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

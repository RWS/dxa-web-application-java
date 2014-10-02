package com.sdl.tridion.referenceimpl.cid;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ContextualMediaHelperTest {

    private final ContextualMediaHelper mediaHelper = new ContextualMediaHelper();

    @Test
    public void testGetResponsiveImageUrl() throws Exception {
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "100%", 3.3, 12), is("/cid/scale/2048x621/source/site/example.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "100%", 3.3, 6), is("/cid/scale/1024x311/source/site/example.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "100%", 2.0, 12), is("/cid/scale/2048x1024/source/site/example.jpg"));

        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "50%", 3.3, 12), is("/cid/scale/1024x311/source/site/example.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "50%", 3.3, 6), is("/cid/scale/640x194/source/site/example.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "75%", 2.0, 12), is("/cid/scale/2048x1024/source/site/example.jpg"));

        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "320", 2.5, 12), is("/cid/scale/320x128/source/site/example.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "321", 2.5, 12), is("/cid/scale/640x256/source/site/example.jpg"));

        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "639", 2.5, 12), is("/cid/scale/640x256/source/site/example.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "640", 2.5, 12), is("/cid/scale/640x256/source/site/example.jpg"));
        assertThat(mediaHelper.getResponsiveImageUrl("/example.jpg", "641", 2.5, 12), is("/cid/scale/1024x410/source/site/example.jpg"));
    }
}

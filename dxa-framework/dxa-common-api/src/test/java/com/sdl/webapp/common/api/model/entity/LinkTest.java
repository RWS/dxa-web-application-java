package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.localization.Localization;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LinkTest {

    @Test
    public void shouldDecideWhetherTheRequestIsInContextOfLink() {
        //given 
        Link link = new Link();
        link.setUrl("/page");

        //when
        assertTrue(link.isCurrentContext("/page", localization("/")));
        assertFalse(link.isCurrentContext("/other", localization("/")));
        assertFalse(link.isCurrentContext("/", localization("/")));
    }

    private Localization localization(String url) {
        Localization localization = mock(Localization.class);
        when(localization.getPath()).thenReturn(url);
        return localization;
    }
}
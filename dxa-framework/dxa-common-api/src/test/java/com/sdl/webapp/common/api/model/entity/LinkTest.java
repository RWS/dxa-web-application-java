package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.PojosTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class LinkTest extends PojosTest {

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
        lenient().when(localization.getPath()).thenReturn(url);
        return localization;
    }

    @Override
    protected Class<?> getPojoClass() {
        return Link.class;
    }
}
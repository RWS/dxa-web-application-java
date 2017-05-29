package com.sdl.dxa.common.dto;

import org.junit.Test;

import static com.sdl.dxa.common.dto.DepthCounter.UNLIMITED_DEPTH;
import static com.sdl.webapp.common.api.navigation.NavigationFilter.DEFAULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SitemapRequestDtoTest {

    @Test
    public void shouldReturnSiteMapRequestDto_WithLowerLevel() {
        //given 
        SitemapRequestDto requestDto = new SitemapRequestDto("1", 2, new DepthCounter(42), null);

        //when
        SitemapRequestDto actual = requestDto.nextExpandLevel();

        //then
        assertEquals(41, actual.getExpandLevels().getCounter());
    }

    @Test
    public void shouldInstantiateBuilder_WithDefaultValues() {
        //given

        //when
        SitemapRequestDto dto = SitemapRequestDto.builder(42).build();

        //then
        assertEquals(UNLIMITED_DEPTH, dto.getExpandLevels());
        assertEquals(42, dto.getLocalizationId());
        assertEquals(DEFAULT, dto.getNavigationFilter());
        assertNull(dto.getSitemapId());
    }
}
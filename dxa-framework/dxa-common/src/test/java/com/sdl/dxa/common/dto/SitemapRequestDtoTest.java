package com.sdl.dxa.common.dto;

import com.sdl.webapp.common.api.navigation.NavigationFilter;
import org.junit.jupiter.api.Test;

import static com.sdl.dxa.common.dto.DepthCounter.UNLIMITED_DEPTH;
import static com.sdl.webapp.common.api.navigation.NavigationFilter.DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SitemapRequestDtoTest {

    @Test
    public void shouldReturnSiteMapRequestDto_WithLowerLevel() {
        //given 
        SitemapRequestDto requestDto = new SitemapRequestDto("1", 2, "tcm", new DepthCounter(42), null);

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

    @Test
    public void shouldReturnWholeTreeRequest() {
        //given 

        //when
        SitemapRequestDto dto = SitemapRequestDto.wholeTree(1).build();

        //then
        assertEquals(1, dto.getLocalizationId());
        assertEquals(UNLIMITED_DEPTH, dto.getExpandLevels());
        assertEquals(new NavigationFilter().setDescendantLevels(-1), dto.getNavigationFilter());
    }
}
package com.sdl.dxa.common.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SitemapRequestDtoTest {

    @Test
    public void shouldReturnSiteMapRequestDto_WithLowerLevel() {
        //given 
        SitemapRequestDto requestDto = new SitemapRequestDto("1", 2, new DepthCounter(42), null);

        //when
        SitemapRequestDto actual = requestDto.nextExpandLevel();

        //then
        assertEquals(41, actual.getExpandLevels().getDeep());
    }
}
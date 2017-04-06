package com.sdl.dxa.common.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PageRequestDtoTest {

    @Test
    public void shouldIgnoreDepthCounter_InEqualsAndHashCodeCheck() {
        //given
        PageRequestDto dto1 = PageRequestDto.builder()
                .includePages(PageRequestDto.PageInclusion.INCLUDE)
                .path("/").build();

        PageRequestDto dto2 = PageRequestDto.builder()
                .includePages(PageRequestDto.PageInclusion.INCLUDE)
                .path("/").build();

        //when
        dto2.depthIncreaseAndCheckIfSafe();

        //then
        assertEquals(dto2, dto1);
        assertEquals(dto2.hashCode(), dto1.hashCode());
    }
}
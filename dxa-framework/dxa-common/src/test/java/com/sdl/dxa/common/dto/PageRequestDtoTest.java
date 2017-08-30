package com.sdl.dxa.common.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PageRequestDtoTest {

    @Test
    public void shouldIgnoreDepthCounter_InEqualsAndHashCodeCheck() {
        //given
        PageRequestDto dto1 = PageRequestDto.builder(42, "/")
                .includePages(PageRequestDto.PageInclusion.INCLUDE).build();

        PageRequestDto dto2 = PageRequestDto.builder(42, "/")
                .includePages(PageRequestDto.PageInclusion.INCLUDE).build();

        //when
        dto2.getDepthCounter().depthIncreaseAndCheckIfSafe();

        //then
        assertEquals(dto2, dto1);
        assertEquals(dto2.hashCode(), dto1.hashCode());
    }

    @Test
    public void shouldSetPublicationId_AndPagePath() {
        //given 

        //when
        PageRequestDto dto = PageRequestDto.builder(42, "/").build();

        //then
        assertEquals(42, dto.getPublicationId());
        assertEquals("/", dto.getPath());
    }

    @Test
    public void shouldSetStringPublicationId() {
        //given 

        //when
        PageRequestDto dto = PageRequestDto.builder("42", "/").build();

        //then
        assertEquals(42, dto.getPublicationId());
    }
}
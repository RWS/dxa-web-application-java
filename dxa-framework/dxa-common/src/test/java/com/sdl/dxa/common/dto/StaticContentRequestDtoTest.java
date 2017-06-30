package com.sdl.dxa.common.dto;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StaticContentRequestDtoTest {

    @Test
    public void shouldHaveSpecificUrl_AsDefault_ForBaseUrl() {
        //given 

        //when
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/", "42").build();

        //then
        // ends with slash, starts with http: of https: with even number of slashes, the rest is not empty
        assertTrue(requestDto.getBaseUrl().matches("^https?:(//)*[^/]+/$"));
    }
}
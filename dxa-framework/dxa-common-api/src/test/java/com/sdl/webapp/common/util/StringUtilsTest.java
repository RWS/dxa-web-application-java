package com.sdl.webapp.common.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

    @Test
    public void shouldCorrectlyConvertFormatStringFromCM() throws Exception {
        //given
        String s1 = "a {0} b {1} c {2} d";
        String s2 = "a {1} b {0} c {2} d";
        String s3 = "a {2} b {1} c {0} d";

        //when
        String r1 = StringUtils.convertFormatStringFromCM(s1);
        String r2 = StringUtils.convertFormatStringFromCM(s2);
        String r3 = StringUtils.convertFormatStringFromCM(s3);

        //then
        assertEquals("a %1$s b %2$s c %3$s d", r1);
        assertEquals("a %2$s b %1$s c %3$s d", r2);
        assertEquals("a %3$s b %2$s c %1$s d", r3);
    }
}
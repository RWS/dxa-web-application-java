package com.sdl.webapp.common.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TcmUtilsTest {

    @Test
    public void shouldBuildPublicationTcmUri() throws Exception {
        //given
        String expected = "tcm:0-2-1";

        //when
        String result = TcmUtils.buildPublicationTcmUri(2);

        //then
        assertEquals(expected, result);
    }

    @Test
    public void shouldBuildTemplateTcmUri() throws Exception {
        //given
        String expected = "tcm:1-2-" + TcmUtils.TEMPLATE_ITEM_TYPE;

        //when
        String result = TcmUtils.buildTemplateTcmUri("1", "2");

        //then
        assertEquals(expected, result);
    }

    @Test
    public void shouldBuildTcmUri() throws Exception {
        //given
        String expected = "tcm:1-2";

        //when
        String result = TcmUtils.buildTcmUri(1, 2);
        String result2 = TcmUtils.buildTcmUri("1", "2");

        //then
        assertEquals(expected, result);
        assertEquals(expected, result2);
    }

    @Test
    public void shouldBuildTcmUri1() throws Exception {
        //given
        String expected = "tcm:1-2-3";

        //when
        String result = TcmUtils.buildTcmUri(1, 2, 3);
        String result2 = TcmUtils.buildTcmUri("1", "2", "3");

        //then
        assertEquals(expected, result);
        assertEquals(expected, result2);
    }
}
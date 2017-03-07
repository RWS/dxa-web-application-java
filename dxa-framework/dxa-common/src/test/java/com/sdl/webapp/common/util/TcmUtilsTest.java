package com.sdl.webapp.common.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TcmUtilsTest {

    @Test
    public void shouldBuildPublicationTcmUri() throws Exception {
        //given
        String expected = "tcm:0-2-1";

        //when
        String result = TcmUtils.buildPublicationTcmUri(2);
        String resultStr = TcmUtils.buildPublicationTcmUri("" + 2);

        //then
        assertEquals(expected, result);
        assertEquals(expected, resultStr);
    }

    @Test
    public void shouldBuildTemplateTcmUri() throws Exception {
        //given
        String expected = "tcm:1-2-32";

        //when
        String result = TcmUtils.buildTemplateTcmUri("1", "2");
        String result2 = TcmUtils.buildTemplateTcmUri(1, 2);

        //then
        assertEquals(expected, result);
        assertEquals(expected, result2);
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
    public void shouldBuildTcmUriForPage() {
        //given
        String expected = "tcm:1-2-64";

        //when
        String result = TcmUtils.buildPageTcmUri("1", "2");

        //then
        assertEquals(expected, result);
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

    @Test
    public void shouldGetItemId() {
        //when
        int itemId = TcmUtils.getItemId("tcm:0-1-2");
        int itemId42 = TcmUtils.getItemId("tcm:0-42-2");
        int itemId2 = TcmUtils.getItemId("tcm:0-1");

        //then
        assertEquals(1, itemId);
        assertEquals(1, itemId2);
        assertEquals(42, itemId42);
    }

    @Test
    public void shouldGetPublicationId() {
        //when
        int getPublicationId = TcmUtils.getPublicationId("tcm:6-1-2");
        int getPublicationId42 = TcmUtils.getPublicationId("tcm:42-1-2");
        int getPublicationId2 = TcmUtils.getPublicationId("tcm:6-1");

        //then
        assertEquals(6, getPublicationId);
        assertEquals(6, getPublicationId2);
        assertEquals(42, getPublicationId42);
    }

    @Test
    public void shouldReturnMinus1WhenTcmIsWrong() {
        //when
        int itemId = TcmUtils.getItemId("tcm:0");
        int itemId1 = TcmUtils.getItemId("tcm:0---");
        int itemId2 = TcmUtils.getItemId("qwe");
        int itemId3 = TcmUtils.getItemId(null);

        int getPublicationId = TcmUtils.getPublicationId("tcm:0");
        int getPublicationId1 = TcmUtils.getPublicationId("tcm:0---");
        int getPublicationId2 = TcmUtils.getPublicationId("qwe");
        int getPublicationId3 = TcmUtils.getPublicationId(null);

        //then
        assertEquals(-1, itemId);
        assertEquals(-1, itemId1);
        assertEquals(-1, itemId2);
        assertEquals(-1, itemId3);
        assertEquals(-1, getPublicationId);
        assertEquals(-1, getPublicationId1);
        assertEquals(-1, getPublicationId2);
        assertEquals(-1, getPublicationId3);
    }

    @Test
    public void shouldLocalizeTcmUriFromTwoUris() {
        //when
        String tcmUri = TcmUtils.localizeTcmUri("tcm:5-6-7", "tcm:0-8-1");
        String tcmUri2 = TcmUtils.localizeTcmUri("tcm:51-6-7", "tcm:0-81-1");

        //then
        assertEquals("tcm:8-6-7", tcmUri);
        assertEquals("tcm:81-6-7", tcmUri2);
    }

    @Test
    public void shouldLocalizeTcmUriFromUriAndId() {
        //when
        String tcmUri = TcmUtils.localizeTcmUri("tcm:5-6-7", 8);
        String tcmUri1 = TcmUtils.localizeTcmUri("tcm:5-6", 8);
        String tcmUri2 = TcmUtils.localizeTcmUri("tcm:51-6-7", 81);

        //then
        assertEquals("tcm:8-6-7", tcmUri);
        assertEquals("tcm:8-6", tcmUri1);
        assertEquals("tcm:81-6-7", tcmUri2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfArgumentIsNotValid() {
        //when
        TcmUtils.localizeTcmUri("qwe", 1);

        //then
        //IAE
    }

    @Test
    public void shouldGetSitemapIDForTaxonomy() {
        //when
        String identifier = TcmUtils.Taxonomies.getTaxonomySitemapIdentifier("1");
        String identifier2 = TcmUtils.Taxonomies.getTaxonomySitemapIdentifier("2");

        //then
        assertEquals("t1", identifier);
        assertEquals("t2", identifier2);
    }

    @Test
    public void shouldGetSitemapIDForTaxonomyPage() {
        //when
        String identifier = TcmUtils.Taxonomies.getTaxonomySitemapIdentifier("1", TcmUtils.Taxonomies.SitemapItemType.PAGE, "2");

        //then
        assertEquals("t1-p2", identifier);
    }

    @Test
    public void shouldGetSitemapIDForTaxonomyKeyword() {
        //when
        String identifier = TcmUtils.Taxonomies.getTaxonomySitemapIdentifier("1", TcmUtils.Taxonomies.SitemapItemType.KEYWORD, "2");

        //then
        assertEquals("t1-k2", identifier);
    }

    @Test
    public void shouldDetectTcmUri() {
        assertTrue(TcmUtils.isTcmUri("tcm:1-2-3"));
        assertTrue(TcmUtils.isTcmUri("tcm:1-2"));
        assertFalse(TcmUtils.isTcmUri("tcm:1"));
        assertFalse(TcmUtils.isTcmUri("not"));
    }

}
package com.sdl.webapp.common.api.navigation;

import com.sdl.webapp.common.api.localization.Localization;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaxonomySitemapItemUrisHolderTest {

    @Test
    public void shouldParseDifferentTypesOfIds() {
        //given
        Localization localization = mock(Localization.class);
        when(localization.getId()).thenReturn("1");

        //when
        TaxonomySitemapItemUrisHolder taxonomyOnly = TaxonomySitemapItemUrisHolder.parse("t2", localization);
        TaxonomySitemapItemUrisHolder keyword = TaxonomySitemapItemUrisHolder.parse("t2-k3", localization);
        TaxonomySitemapItemUrisHolder page = TaxonomySitemapItemUrisHolder.parse("t2-p4", localization);
        TaxonomySitemapItemUrisHolder badFormat = TaxonomySitemapItemUrisHolder.parse("non-compliant", localization);
        TaxonomySitemapItemUrisHolder toNull = TaxonomySitemapItemUrisHolder.parse("", localization);
        TaxonomySitemapItemUrisHolder toNull2 = TaxonomySitemapItemUrisHolder.parse(null, localization);

        //then
        assertNotNull(taxonomyOnly);
        assertEquals("tcm:1-2-512", taxonomyOnly.getTaxonomyUri());
        assertNull(taxonomyOnly.getPageUri());
        assertNull(taxonomyOnly.getKeywordUri());
        assertTrue(taxonomyOnly.isTaxonomyOnly());

        assertNotNull(keyword);
        assertEquals("tcm:1-2-512", keyword.getTaxonomyUri());
        assertNull(keyword.getPageUri());
        assertEquals("tcm:1-3-1024", keyword.getKeywordUri());
        assertTrue(keyword.isKeyword());

        assertNotNull(page);
        assertEquals("tcm:1-2-512", page.getTaxonomyUri());
        assertEquals("tcm:1-4-64", page.getPageUri());
        assertNull(page.getKeywordUri());
        assertTrue(page.isPage());

        assertNull(toNull);
        assertNull(toNull2);
        assertNull(badFormat);
    }

}
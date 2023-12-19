package com.sdl.webapp.common.api.navigation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaxonomyUrisHolderTest {

    @Test
    public void shouldParseDifferentTypesOfIds() {
        //given

        //when
        TaxonomyUrisHolder taxonomyOnly = TaxonomyUrisHolder.parse("t2", "1");
        TaxonomyUrisHolder keyword = TaxonomyUrisHolder.parse("t2-k3", "1");
        TaxonomyUrisHolder page = TaxonomyUrisHolder.parse("t2-p4", "1");
        TaxonomyUrisHolder badFormat = TaxonomyUrisHolder.parse("non-compliant", "1");
        TaxonomyUrisHolder toNull = TaxonomyUrisHolder.parse("", "1");
        TaxonomyUrisHolder toNull2 = TaxonomyUrisHolder.parse(null, "1");

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

    @Test
    public void shouldXor_TaxonomyOnly_PageOrKeyword() {
        //given 

        //when
        TaxonomyUrisHolder holder = TaxonomyUrisHolder.parse("t2", "1");
        TaxonomyUrisHolder holder2 = TaxonomyUrisHolder.parse("t2-k1", "1");
        TaxonomyUrisHolder holder3 = TaxonomyUrisHolder.parse("t2-p1", "1");

        //then
        assertNotNull(holder);
        assertNotNull(holder2);
        assertNotNull(holder3);
        assertNotEquals(holder.isTaxonomyOnly(), holder.isKeyword() || holder.isPage());
        assertNotEquals(holder2.isTaxonomyOnly(), holder2.isKeyword() || holder2.isPage());
        assertNotEquals(holder3.isTaxonomyOnly(), holder3.isKeyword() || holder3.isPage());
    }

}
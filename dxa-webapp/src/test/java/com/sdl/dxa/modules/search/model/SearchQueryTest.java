package com.sdl.dxa.modules.search.model;

import com.sdl.webapp.common.api.model.RichText;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class SearchQueryTest {

    @Test
    public void shouldNotFailWithNPEWhenFormattingTextResults() throws Exception {
        //given
        SearchQuery searchQuery = new SearchQuery();

        //when
        String text = searchQuery.formatResultsText();
        String textNo = searchQuery.formatNoResultsText();

        //then
        assertEquals("", text);
        assertEquals("", textNo);


        //given
        searchQuery.setResultsText(new RichText("before {0} after"));
        searchQuery.setNoResultsText(new RichText("before {0} after"));

        //when
        String text2 = searchQuery.formatResultsText();
        String text2No = searchQuery.formatNoResultsText();

        //then
        assertEquals("before  after", text2);
        assertEquals("before  after", text2No);


        //given
        searchQuery.setNoResultsText(null);
        searchQuery.setResultsText(null);
        searchQuery.setQueryDetails(new SearchQuery.QueryDetails("search", Collections.emptyMap()));

        //when
        String text3 = searchQuery.formatResultsText();
        String text3No = searchQuery.formatNoResultsText();

        //then
        assertEquals("", text3);
        assertEquals("", text3No);
    }

    @Test
    public void shouldFormatNoResultsText() {
        //given
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setNoResultsText(new RichText("before {0} after"));
        searchQuery.setQueryDetails(new SearchQuery.QueryDetails("search", Collections.emptyMap()));

        //when
        String text = searchQuery.formatNoResultsText();

        //then
        assertEquals("before search after", text);
    }

}
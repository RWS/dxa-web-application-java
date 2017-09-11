package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class BaseFormatterTest {

    @Test
    public void shouldAddMediaType() {
        //given
        String expected = "test";
        BaseFormatter baseFormatter = getBaseFormatter(expected);

        //when
        baseFormatter._addMediaType(expected);
        List<String> list = baseFormatter.getValidTypes(Collections.singletonList(expected));

        //then
        assertTrue(list.size() == 1);
        assertTrue(list.contains(expected));
    }

    @Test
    public void shouldReturnValidTypes() {
        //given
        BaseFormatter baseFormatter = getBaseFormatter("application/json,application/test");
        String expected = "application/test";

        //when
        List<String> types = baseFormatter.getValidTypes(Collections.singletonList(expected));

        //then
        assertTrue(types.contains(expected));
        assertTrue(types.size() == 1);
    }

    @Test
    public void shouldReturnEmptyListForEmptyHeader() {
        //given
        BaseFormatter baseFormatter = new TestBaseFormatter(new MockHttpServletRequest(), null);

        //when
        List<String> validTypes = baseFormatter.getValidTypes(Collections.singletonList(""));

        //then
        assertTrue(validTypes.isEmpty());
    }

    @Test
    public void shouldFilterOutAllowedTypes() {
        //given
        BaseFormatter baseFormatter = getBaseFormatter("application/json,application/test");

        //when
        List<String> types = baseFormatter.getValidTypes(Collections.singletonList("hello"));

        //then
        assertTrue(types.isEmpty());
    }

    @Test
    public void shouldReturnDefaultIsProcessModel() {
        //given
        @NotNull BaseFormatter baseFormatter = getBaseFormatter(null);

        //when
        boolean processModel = baseFormatter.isProcessModel();

        //then
        assertFalse(processModel);
    }

    @Test
    public void shouldReturnDefaultIsAddIncludes() {
        //given
        @NotNull BaseFormatter baseFormatter = getBaseFormatter(null);

        //when
        boolean addIncludes = baseFormatter.isAddIncludes();

        //then
        assertFalse(addIncludes);
    }

    @Test
    public void shouldScoreAsZeroIfNoMediaTypesAdded() {
        //given
        @NotNull BaseFormatter baseFormatter = getBaseFormatter(null);

        //when
        double score = baseFormatter.score();

        //then
        assertEquals(0.0, score);
    }

    @Test
    public void shouldGetPreferableScoreForCurrentFormatter() {
        //given
        @NotNull BaseFormatter baseFormatter = getBaseFormatter("text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c");
        baseFormatter._addMediaType("text/x-dvi");

        //when
        double score = baseFormatter.score();

        //then
        assertEquals(0.8, score, 0.0);
    }

    @Test
    public void shouldCallInternalMethod_FromDeprecated() {
        //given
        @NotNull BaseFormatter baseFormatter = getBaseFormatter("text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c");
        baseFormatter.addMediaType("text/x-dvi");

        //when
        double score = baseFormatter.score();

        //then
        assertEquals(0.8, score, 0.0);
    }

    @Test
    public void shouldSelectBestPreferableScoreForCurrentFormatter() {
        //given
        @NotNull BaseFormatter baseFormatter = getBaseFormatter("text/plain; q=0.5, text/html, text/x-dvi; q=0.8, text/x-c");
        baseFormatter._addMediaType("text/x-dvi");
        baseFormatter._addMediaType("text/html");

        //when
        double score = baseFormatter.score();

        //then
        assertEquals(1.0, score, 0.0);
    }

    @NotNull
    private BaseFormatter getBaseFormatter(String acceptHeader) {
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        if (acceptHeader != null) {
            httpServletRequest.addHeader("Accept", acceptHeader);
        }
        return new TestBaseFormatter(httpServletRequest, null);
    }

    private static class TestBaseFormatter extends BaseFormatter {

        TestBaseFormatter(HttpServletRequest request, WebRequestContext context) {
            super(request, context);
        }

        @Override
        public Object formatData(Object model) {
            return null;
        }

        @Override
        public Object getSyndicationItem(FeedItem item) {
            return null;
        }
    }
}
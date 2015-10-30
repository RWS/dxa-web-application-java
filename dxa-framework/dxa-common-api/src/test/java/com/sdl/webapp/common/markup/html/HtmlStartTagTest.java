package com.sdl.webapp.common.markup.html;

import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlStartTag;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@code HtmlStartTag}.
 */
public class HtmlStartTagTest {

    @Test
    public void testRenderHtmlNoAttributes() {
        final HtmlStartTag tag = new HtmlStartTag("div", Collections.<HtmlAttribute>emptyList());
        assertThat(tag.renderHtml(), is("<div>"));
    }

    @Test
    public void testRenderHtmlSingleAttribute() {
        final HtmlStartTag tag = new HtmlStartTag("div", Arrays.asList(new HtmlAttribute("class", "header")));
        assertThat(tag.renderHtml(), is("<div class=\"header\">"));
    }

    @Test
    public void testRenderHtmlWithAttributes() {
        final HtmlStartTag tag = new HtmlStartTag("div",
                Arrays.asList(new HtmlAttribute("class", "header"), new HtmlAttribute("id", "14785")));
        assertThat(tag.renderHtml(), is("<div class=\"header\" id=\"14785\">"));
    }
}

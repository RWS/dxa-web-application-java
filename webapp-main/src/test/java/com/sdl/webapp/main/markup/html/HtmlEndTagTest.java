package com.sdl.webapp.main.markup.html;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@code HtmlEndTag}.
 */
public class HtmlEndTagTest {

    @Test
    public void testRenderHtml() {
        final HtmlEndTag tag = new HtmlEndTag("div");
        assertThat(tag.renderHtml(), is("</div>"));
    }
}

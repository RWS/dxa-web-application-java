package com.sdl.webapp.common.markup.html;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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

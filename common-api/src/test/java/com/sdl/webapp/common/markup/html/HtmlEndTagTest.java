package com.sdl.webapp.common.markup.html;

import com.sdl.webapp.common.markup.html.HtmlEndTag;
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

package com.sdl.webapp.common.markup.html;

import com.sdl.webapp.common.markup.html.HtmlAttribute;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@code HtmlAttribute}.
 */
public class HtmlAttributeTest {

    @Test
    public void testRenderHtml() {
        assertThat(new HtmlAttribute("x", "y").renderHtml(), is("x=\"y\""));
        assertThat(new HtmlAttribute("hello", "a \" quote").renderHtml(), is("hello=\"a &quot; quote\""));
        assertThat(new HtmlAttribute("hello", "&&").renderHtml(), is("hello=\"&amp;&amp;\""));
        assertThat(new HtmlAttribute("hello", "<").renderHtml(), is("hello=\"&lt;\""));
        assertThat(new HtmlAttribute("test", "&\"<>&\"").renderHtml(), is("test=\"&amp;&quot;&lt;>&amp;&quot;\""));
    }
}

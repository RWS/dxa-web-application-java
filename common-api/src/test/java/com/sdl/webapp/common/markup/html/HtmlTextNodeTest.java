package com.sdl.webapp.common.markup.html;

import com.sdl.webapp.common.markup.html.HtmlTextNode;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@code HtmlTextNode}.
 */
public class HtmlTextNodeTest {

    @Test
    public void testRenderHtmlWithoutEscape() {
        final HtmlTextNode node = new HtmlTextNode("Hello <> World & &amp;", false);
        assertThat(node.renderHtml(), is("Hello <> World & &amp;"));
    }

    @Test
    public void testRenderHtmlWithEscape() {
        final HtmlTextNode node = new HtmlTextNode("Hello <> World & &amp;", true);
        assertThat(node.renderHtml(), is("Hello &lt;&gt; World &amp; &amp;amp;"));
    }

    @Test
    public void testRenderHtmlDefault() {
        final HtmlTextNode node = new HtmlTextNode("\"Nice\"");
        assertThat("Default should be with HTML escaping", node.renderHtml(), is("&quot;Nice&quot;"));
    }
}

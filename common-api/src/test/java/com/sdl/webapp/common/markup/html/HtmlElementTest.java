package com.sdl.webapp.common.markup.html;

import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.HtmlTextNode;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@code HtmlElement}.
 */
public class HtmlElementTest {

    @Test
    public void testRenderHtmlWithCloseTag() {
        final HtmlElement element = new HtmlElement("div", true, Arrays.asList(new HtmlAttribute("id", "123")),
                Collections.<HtmlNode>emptyList());
        assertThat(element.renderHtml(), is("<div id=\"123\"></div>"));
    }

    @Test
    public void testRenderHtmlWithoutCloseTag() {
        final HtmlElement element = new HtmlElement("img", false,
                Arrays.asList(new HtmlAttribute("src", "http://www.example.com/image.jpg"),
                        new HtmlAttribute("alt", "Example Image")), Collections.<HtmlNode>emptyList());
        assertThat(element.renderHtml(), is("<img src=\"http://www.example.com/image.jpg\" alt=\"Example Image\">"));
    }

    @Test
    public void testRenderHtmlWithContent() {
        final HtmlElement element = new HtmlElement("p", true, Arrays.asList(new HtmlAttribute("class", "text")),
                Arrays.<HtmlNode>asList(new HtmlTextNode("  This is some <text>!")));
        assertThat(element.renderHtml(), is("<p class=\"text\">  This is some &lt;text&gt;!</p>"));
    }
}

package com.sdl.webapp.common.markup.html;

import com.sdl.webapp.common.markup.html.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@code HtmlMultiNode}.
 */
public class HtmlMultiNodeTest {

    @Test
    public void testRenderHtml() {
        final HtmlMultiNode node = new HtmlMultiNode(
                new HtmlElement("h1", true, Collections.<HtmlAttribute>emptyList(),
                        Arrays.<HtmlNode>asList(new HtmlTextNode("Example"))),
                new HtmlElement("p", true, Collections.<HtmlAttribute>emptyList(),
                        Arrays.<HtmlNode>asList(new HtmlTextNode("This is a test."))));
        assertThat(node.renderHtml(), is("<h1>Example</h1><p>This is a test.</p>"));
    }
}

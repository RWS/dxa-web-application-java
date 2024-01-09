package com.sdl.webapp.common.markup.html;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@code HtmlCommentNode}.
 */
public class HtmlCommentNodeTest {

    @Test
    public void testRenderHtmlSimple() {
        final HtmlCommentNode node = new HtmlCommentNode("Hello World");
        assertThat(node.renderHtml(), is("<!-- Hello World -->"));
    }

    @Test
    public void testRenderHtmlWithDelimiters() {
        final HtmlCommentNode node = new HtmlCommentNode("Hello <!--Worl-->d");
        assertThat(node.renderHtml(), is("<!-- Hello World -->"));
    }
}

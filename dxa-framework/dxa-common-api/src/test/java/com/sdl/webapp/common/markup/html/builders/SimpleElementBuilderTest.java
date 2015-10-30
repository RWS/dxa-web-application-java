package com.sdl.webapp.common.markup.html.builders;

import com.sdl.webapp.common.markup.html.*;
import com.sdl.webapp.common.markup.html.builders.SimpleElementBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@code SimpleElementBuilder}.
 */
public class SimpleElementBuilderTest {

    @Test
    public void testBasic() {
        final SimpleElementBuilder builder = new SimpleElementBuilder("div", true);
        assertThat(builder.build(), is(new HtmlElement("div", true, Collections.<HtmlAttribute>emptyList(),
                Collections.<HtmlNode>emptyList())));
    }

    @Test
    public void testWithAttributes() {
        final HtmlAttribute attr1 = new HtmlAttribute("class", "highlight");
        final SimpleElementBuilder builder = new SimpleElementBuilder("p", false)
                .withAttribute(attr1).withAttribute("id", "test99");
        assertThat(builder.build(), is(new HtmlElement("p", false,
                Arrays.asList(attr1, new HtmlAttribute("id", "test99")),
                Collections.<HtmlNode>emptyList())));
    }

    @Test
    public void testWithAttributeIfNotEmpty() {
        final SimpleElementBuilder builder = new SimpleElementBuilder("div", true)
                .withAttributeIfNotEmpty("aap", "")
                .withAttributeIfNotEmpty("noot", "kastanje")
                .withAttributeIfNotEmpty("mies", null);
        assertThat(builder.build(), is(new HtmlElement("div", true,
                Arrays.asList(new HtmlAttribute("noot", "kastanje")),
                Collections.<HtmlNode>emptyList())));
    }

    @Test
    public void testWithId() {
        final SimpleElementBuilder builder = new SimpleElementBuilder("div", true).withId("placeholder");
        assertThat(builder.build(), is(new HtmlElement("div", true,
                Arrays.asList(new HtmlAttribute("id", "placeholder")),
                Collections.<HtmlNode>emptyList())));
    }

    @Test
    public void testWithClassNotEmpty() {
        final SimpleElementBuilder builder = new SimpleElementBuilder("div", true).withClass("pretty");
        assertThat(builder.build(), is(new HtmlElement("div", true,
                Arrays.asList(new HtmlAttribute("class", "pretty")),
                Collections.<HtmlNode>emptyList())));
    }

    @Test
    public void testWithClassEmpty() {
        final SimpleElementBuilder builder = new SimpleElementBuilder("div", true).withClass("");
        assertThat("Class attribute should not be included if value is empty", builder.build(),
                is(new HtmlElement("div", true, Collections.<HtmlAttribute>emptyList(),
                        Collections.<HtmlNode>emptyList())));
    }

    @Test
    public void testWithContentNode() {
        final SimpleElementBuilder builder = new SimpleElementBuilder("div", true)
                .withContent(new HtmlCommentNode("Comment"));
        assertThat(builder.build(), is(new HtmlElement("div", true,
                Collections.<HtmlAttribute>emptyList(),
                Arrays.<HtmlNode>asList(new HtmlCommentNode("Comment")))));
    }

    @Test
    public void testWithContentText() {
        final SimpleElementBuilder builder = new SimpleElementBuilder("div", true)
                .withContent("Tom & Jerry");
        assertThat(builder.build(), is(new HtmlElement("div", true,
                Collections.<HtmlAttribute>emptyList(),
                Arrays.<HtmlNode>asList(new HtmlTextNode("Tom & Jerry", true)))));
    }

    @Test
    public void testWithLiteralContent() {
        final SimpleElementBuilder builder = new SimpleElementBuilder("div", true)
                .withLiteralContent("Tom & Jerry");
        assertThat(builder.build(), is(new HtmlElement("div", true,
                Collections.<HtmlAttribute>emptyList(),
                Arrays.<HtmlNode>asList(new HtmlTextNode("Tom & Jerry", false)))));
    }
}

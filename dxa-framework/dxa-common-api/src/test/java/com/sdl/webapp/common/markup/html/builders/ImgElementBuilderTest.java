package com.sdl.webapp.common.markup.html.builders;

import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.HtmlNode;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for {@code AnchorElementBuilder}.
 */
public class ImgElementBuilderTest {

    @Test
    public void testBuild() {
        final ImgElementBuilder builder = new ImgElementBuilder().withSrc("http://www.example.com/logo.png")
                .withAlt("Logo").withWidth(120).withHeight(80);
        assertThat(builder.build(), is(new HtmlElement("img", false,
                Arrays.asList(
                        new HtmlAttribute("src", "http://www.example.com/logo.png"),
                        new HtmlAttribute("alt", "Logo"),
                        new HtmlAttribute("width", "120"),
                        new HtmlAttribute("height", "80")),
                Collections.<HtmlNode>emptyList())));
    }
}

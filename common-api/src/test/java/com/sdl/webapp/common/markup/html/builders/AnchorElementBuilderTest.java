package com.sdl.webapp.common.markup.html.builders;

import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.builders.AnchorElementBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@code AnchorElementBuilder}.
 */
public class AnchorElementBuilderTest {

    @Test
    public void testBuild() {
        final AnchorElementBuilder builder = new AnchorElementBuilder().withHref("http://www.sdl.com").withTitle("SDL");
        assertThat(builder.build(), is(new HtmlElement("a", true,
                Arrays.asList(new HtmlAttribute("href", "http://www.sdl.com"), new HtmlAttribute("title", "SDL")),
                Collections.<HtmlNode>emptyList())));
    }
}

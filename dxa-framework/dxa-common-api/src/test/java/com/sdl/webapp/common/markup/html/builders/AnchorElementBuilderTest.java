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
public class AnchorElementBuilderTest {

    @Test
    public void testBuild() {
        final AnchorElementBuilder builder = new AnchorElementBuilder().withHref("http://www.sdl.com").withTitle("SDL");
        assertThat(builder.build(), is(new HtmlElement("a", true,
                Arrays.asList(new HtmlAttribute("href", "http://www.sdl.com"), new HtmlAttribute("title", "SDL")),
                Collections.<HtmlNode>emptyList())));
    }
}

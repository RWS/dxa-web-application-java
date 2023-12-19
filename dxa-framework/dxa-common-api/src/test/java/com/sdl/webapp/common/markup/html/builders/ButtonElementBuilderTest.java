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
 * Unit tests for {@code ButtonElementBuilder}.
 */
public class ButtonElementBuilderTest {

    @Test
    public void testBuild() {
        final ButtonElementBuilder builder = new ButtonElementBuilder().ofType("submit");
        assertThat(builder.build(), is(new HtmlElement("button", true,
                Arrays.asList(new HtmlAttribute("type", "submit")),
                Collections.<HtmlNode>emptyList())));
    }
}

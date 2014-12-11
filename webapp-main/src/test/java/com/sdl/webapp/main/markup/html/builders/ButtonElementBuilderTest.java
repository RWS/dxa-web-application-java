package com.sdl.webapp.main.markup.html.builders;

import com.sdl.webapp.main.markup.html.HtmlAttribute;
import com.sdl.webapp.main.markup.html.HtmlElement;
import com.sdl.webapp.main.markup.html.HtmlNode;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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

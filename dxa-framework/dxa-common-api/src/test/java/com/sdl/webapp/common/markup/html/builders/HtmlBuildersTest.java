package com.sdl.webapp.common.markup.html.builders;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@code HtmlBuilders}.
 */
public class HtmlBuildersTest {

    @Test
    public void testElement() {
        assertThat(HtmlBuilders.element("div").build().toHtml(), is("<div></div>"));
        assertThat(HtmlBuilders.element("p", false).build().toHtml(), is("<p>"));
        assertThat(HtmlBuilders.element("p", true).build().toHtml(), is("<p></p>"));
    }

    @Test
    public void testDiv() {
        assertThat(HtmlBuilders.div().build().toHtml(), is("<div></div>"));
    }

    @Test
    public void testI() {
        assertThat(HtmlBuilders.i().build().toHtml(), is("<i></i>"));
    }

    @Test
    public void testIframe() {
        assertThat(HtmlBuilders.iframe().build().toHtml(), is("<iframe></iframe>"));
    }

    @Test
    public void testA() {
        assertThat(HtmlBuilders.a().build().toHtml(), is("<a></a>"));
        assertThat(HtmlBuilders.a("http://localhost:8080").build().toHtml(), is("<a href=\"http://localhost:8080\"></a>"));
    }

    @Test
    public void testButton() {
        assertThat(HtmlBuilders.button().build().toHtml(), is("<button></button>"));
        assertThat(HtmlBuilders.button("clear").build().toHtml(), is("<button type=\"clear\"></button>"));
    }

    @Test
    public void testImg() {
        assertThat(HtmlBuilders.img().build().toHtml(), is("<img>"));
        assertThat(HtmlBuilders.img("hello.png").build().toHtml(), is("<img src=\"hello.png\">"));
    }
}

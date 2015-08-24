package com.sdl.webapp.common.markup.html;

import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * ParsableHtmlNodeTest
 *
 * @author nic
 */
public class ParsableHtmlNodeTest {

    @Test
    public void testInjectMarkup() throws Exception {

        String htmlTxt = "<div id='region'><p>Hello there!!!</p><a href='#'>LINK</a></div>";
        ParsableHtmlNode markup = new ParsableHtmlNode(htmlTxt);
        Element html =  markup.getHtmlElement();
        String xpmMarkup = "<!-- Start Component Presentation -->";
        html.prepend(xpmMarkup);

        System.out.println("Processed HTML: " + markup.toHtml());

        assertThat("XPM markup has been injected into HTML", markup.toHtml(), is("<div id=\"region\">\n <!-- Start Component Presentation -->\n <p>Hello there!!!</p>\n <a href=\"#\">LINK</a>\n</div>"));
    }

}

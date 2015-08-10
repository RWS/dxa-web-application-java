package com.sdl.webapp.main.taglib.xpm;

import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.markup.html.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@code XpmPageMarkupTag}.
 */
public class XpmPageMarkupTagTest {

    @Test
    public void testGenerateXpmMarkup() {
        final Map<String, String> pageData = new HashMap<>();
        pageData.put("PageID", "P123");
        pageData.put("PageModified", "2014-12-10T09:34:56+01:00");
        pageData.put("PageTemplateID", "T456");
        pageData.put("PageTemplateModified", "2014-11-09T13:44:21+01:00");
        pageData.put("CmsUrl", "http://localhost:8080/cms");

        final HtmlNode expected = new HtmlMultiNode(
                new HtmlCommentNode("Page Settings: {\"PageID\":\"P123\",\"PageModified\":" +
                        "\"2014-12-10T09:34:56+01:00\",\"PageTemplateID\":\"T456\",\"PageTemplateModified\":" +
                        "\"2014-11-09T13:44:21+01:00\"}"),
                new HtmlElement("script", true,
                        Arrays.asList(
                                new HtmlAttribute("type", "text/javascript"),
                                new HtmlAttribute("language", "javascript"),
                                new HtmlAttribute("defer", "defer"),
                                new HtmlAttribute("src", "http://localhost:8080/cms/WebUI/Editors/SiteEdit/Views/Bootstrap/Bootstrap.aspx?mode=js"),
                                new HtmlAttribute("id", "tridion.siteedit")),
                        Collections.<HtmlNode>emptyList()));

        assertThat(setup(pageData).generateXpmMarkup(), is(expected));
    }

    private XpmPageMarkupTag setup(Map<String, String> pageData) {
        final Page page = mock(Page.class);
        when(page.getPageData()).thenReturn(pageData);

        final XpmPageMarkupTag tag = new XpmPageMarkupTag();
        tag.setPage(page);

        return tag;
    }

}

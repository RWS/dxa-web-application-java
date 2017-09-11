package com.sdl.webapp.common.impl.taglib.xpm;

import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.HtmlMultiNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@code XpmPageMarkupTag}.
 */
public class XpmPageMarkupTagTest {

    @Test
    public void testGenerateXpmMarkup() {
        final Map<String, Object> pageData = new HashMap<>();
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
                        Collections.emptyList()));


        //todo : fix these unit tests
        //assertThat(setup(pageData).generateXpmMarkup(), is(expected));
    }

    private XpmPageMarkupTag setup(Map<String, Object> pageData) {
        final PageModel page = mock(PageModel.class);
        when(page.getXpmMetadata()).thenReturn(pageData);

        final XpmPageMarkupTag tag = new XpmPageMarkupTag();
        tag.setPage(page);

        return tag;
    }

}

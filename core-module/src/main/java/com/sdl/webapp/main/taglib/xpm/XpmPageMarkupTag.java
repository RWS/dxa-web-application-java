package com.sdl.webapp.main.taglib.xpm;

import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlMultiNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;

import java.util.Map;

public class XpmPageMarkupTag extends XpmMarkupTag {

    private static final String PAGE_PATTERN = "Page Settings: {\"PageID\":\"%s\",\"PageModified\":\"%s\"," +
            "\"PageTemplateID\":\"%s\",\"PageTemplateModified\":\"%s\"}";

    private static final HtmlAttribute SCRIPT_TYPE_ATTR = new HtmlAttribute("type", "text/javascript");
    private static final HtmlAttribute SCRIPT_LANG_ATTR = new HtmlAttribute("language", "javascript");
    private static final HtmlAttribute SCRIPT_DEFER_ATTR = new HtmlAttribute("defer", "defer");

    private static final String SCRIPT_ID = "tridion.siteedit";

    private Page page;

    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    protected HtmlNode generateXpmMarkup() {
        final Map<String, String> pageData = page.getPageData();

        final String pageId = pageData.get("PageID");
        final String pageModified = pageData.get("PageModified");
        final String pageTemplateId = pageData.get("PageTemplateID");
        final String pageTemplateModified = pageData.get("PageTemplateModified");

        final String cmsUrl = pageData.get("CmsUrl");

        return new HtmlMultiNode(
                new HtmlCommentNode(String.format(PAGE_PATTERN, pageId, pageModified, pageTemplateId, pageTemplateModified)),
                HtmlBuilders.element("script")
                        .withAttribute(SCRIPT_TYPE_ATTR)
                        .withAttribute(SCRIPT_LANG_ATTR)
                        .withAttribute(SCRIPT_DEFER_ATTR)
                        .withAttribute("src", cmsUrl + "/WebUI/Editors/SiteEdit/Views/Bootstrap/Bootstrap.aspx?mode=js")
                        .withId(SCRIPT_ID)
                        .build()
        );
    }
}

package com.sdl.webapp.common.impl.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlNode;
import lombok.Setter;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Setter
public class XpmPageMarkupTag extends XpmMarkupTag {

    private static final String PAGE_PATTERN = "Page Settings: {\"PageID\":\"%s\",\"PageModified\":\"%s\"," +
            "\"PageTemplateID\":\"%s\",\"PageTemplateModified\":\"%s\"}";

    private static final HtmlAttribute SCRIPT_TYPE_ATTR = new HtmlAttribute("type", "text/javascript");
    private static final HtmlAttribute SCRIPT_LANG_ATTR = new HtmlAttribute("language", "javascript");
    private static final HtmlAttribute SCRIPT_DEFER_ATTR = new HtmlAttribute("defer", "defer");

    private static final String SCRIPT_ID = "tridion.siteedit";

    private PageModel page;

    /**
     * {@inheritDoc}
     */
    @Override
    protected HtmlNode generateXpmMarkup() {

        //noinspection ReturnOfInnerClass
        return new HtmlNode() {
            @Override
            public String renderHtml() {
                return page.getXpmMarkup(getLocalization());
            }
        };

    }

    Localization getLocalization() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).getLocalization();
    }
}

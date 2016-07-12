package com.sdl.webapp.common.impl.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.markup.html.HtmlNode;
import lombok.Setter;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Setter
public class XpmEntityMarkupTag extends XpmMarkupTag {

    private EntityModel entity;

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlNode generateXpmMarkup() {
        //noinspection ReturnOfInnerClass
        return new HtmlNode() {
            @Override
            public String renderHtml() {
                return entity.getXpmMarkup(getLocalization());
            }
        };

    }

    private Localization getLocalization() {
        if (pageContext == null)
            return null;
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).getLocalization();
    }
}

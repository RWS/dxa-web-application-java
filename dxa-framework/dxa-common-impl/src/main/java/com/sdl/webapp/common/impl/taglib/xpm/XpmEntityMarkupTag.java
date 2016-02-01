package com.sdl.webapp.common.impl.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.markup.html.HtmlNode;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>XpmEntityMarkupTag class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class XpmEntityMarkupTag extends XpmMarkupTag {

    private EntityModel entity;

    /**
     * <p>Setter for the field <code>entity</code>.</p>
     *
     * @param entity a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     */
    public void setEntity(EntityModel entity) {
        this.entity = entity;
    }

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

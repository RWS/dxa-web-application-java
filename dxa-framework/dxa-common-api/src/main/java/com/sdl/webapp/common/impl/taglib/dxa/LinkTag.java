package com.sdl.webapp.common.impl.taglib.dxa;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import lombok.Setter;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Setter
public class LinkTag extends HtmlElementTag {

    private Link link;

    private String cssClass;

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlElement generateElement() {
        String linkText = link.getLinkText();
        if (Strings.isNullOrEmpty(linkText)) {
            linkText = getLocalization().getResource("core.readMoreLinkText");
        }

        return HtmlBuilders.a(link.getUrl())
                .withTitle(link.getAlternateText())
                .withClass(cssClass)
                .withTextualContent(linkText)
                .build();
    }

    private Localization getLocalization() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).getLocalization();
    }
}

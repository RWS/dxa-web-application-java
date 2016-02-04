package com.sdl.webapp.common.impl.taglib.dxa;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>LinkTag class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class LinkTag extends HtmlElementTag {

    private Link link;

    private String cssClass;

    /**
     * <p>Setter for the field <code>link</code>.</p>
     *
     * @param link a {@link com.sdl.webapp.common.api.model.entity.Link} object.
     */
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     * <p>Setter for the field <code>cssClass</code>.</p>
     *
     * @param cssClass a {@link java.lang.String} object.
     */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

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

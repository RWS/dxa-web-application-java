package com.sdl.webapp.main.taglib;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.main.markup.html.HtmlElement;
import com.sdl.webapp.main.markup.html.builders.HtmlBuilders;
import com.sdl.webapp.main.markup.html.HtmlTextNode;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class LinkTag extends TagSupport {

    private Link link;

    private String cssClass;

    public void setLink(Link link) {
        this.link = link;
    }

    public void setClass(String cssClass) {
        this.cssClass = cssClass;
    }

    @Override
    public int doStartTag() throws JspException {
        String linkText = link.getLinkText();
        if (Strings.isNullOrEmpty(linkText)) {
            linkText = getLocalization().getResource("core.readMoreLinkText");
        }

        HtmlElement element = HtmlBuilders.a(link.getUrl())
                .withTitle(link.getAlternateText())
                .withClass(cssClass)
                .withContent(linkText)
                .build();

        final JspWriter out = pageContext.getOut();
        try {
            out.write(element.toHtml());
        } catch (IOException e) {
            throw new JspException(e);
        }

        return SKIP_BODY;
    }

    private Localization getLocalization() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).getLocalization();
    }
}

package com.sdl.webapp.main.taglib;

import com.google.common.base.Strings;
import com.sdl.webapp.common.config.Localization;
import com.sdl.webapp.common.config.WebRequestContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class ResourceTag extends TagSupport {

    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int doStartTag() throws JspException {
        final Localization localization = WebApplicationContextUtils.getRequiredWebApplicationContext(
                pageContext.getServletContext()).getBean(WebRequestContext.class).getLocalization();

        final String resource = localization.getResource(key);
        if (!Strings.isNullOrEmpty(resource)) {
            final JspWriter out = pageContext.getOut();
            try {
                out.write(resource);
            } catch (IOException e) {
                throw new JspException(e);
            }
        }

        return SKIP_BODY;
    }
}

package com.sdl.webapp.common.impl.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class XpmIfEnabledTag extends TagSupport {

    @Override
    public int doStartTag() throws JspException {
        return isPreview() ? EVAL_BODY_INCLUDE : SKIP_BODY;
    }

    /**
     * Checks if XPM Preview is enabled.
     *
     * @return whether this is preview mode
     */
    protected boolean isPreview() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).isPreview();
    }
}

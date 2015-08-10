package com.sdl.webapp.main.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class XpmIfEnabledTag extends TagSupport {

    @Override
    public int doStartTag() throws JspException {
        return isPreview() ? EVAL_BODY_INCLUDE : SKIP_BODY;
    }

    protected boolean isPreview() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).isPreview();
    }
}

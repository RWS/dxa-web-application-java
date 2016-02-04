package com.sdl.webapp.common.impl.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * <p>XpmIfEnabledTag class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class XpmIfEnabledTag extends TagSupport {

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        return isPreview() ? EVAL_BODY_INCLUDE : SKIP_BODY;
    }

    /**
     * <p>isPreview.</p>
     *
     * @return a boolean.
     */
    protected boolean isPreview() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext())
                .getBean(WebRequestContext.class).isPreview();
    }
}

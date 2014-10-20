package com.sdl.webapp.main.taglib;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.Localization;
import com.sdl.webapp.common.impl.WebRequestContext;
import com.sdl.webapp.main.RequestAttributeNames;
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
        final Localization localization = (Localization) pageContext.getRequest().getAttribute(RequestAttributeNames.LOCALIZATION);
        if (localization == null) {
            throw new IllegalStateException("Localization is not available. Please make sure that the " +
                    "LocalizationResolverInterceptor is registered.");
        }

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

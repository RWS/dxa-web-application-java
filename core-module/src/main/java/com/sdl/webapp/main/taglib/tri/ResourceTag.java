package com.sdl.webapp.main.taglib.tri;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.WebRequestContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.text.MessageFormat;

public class ResourceTag extends TagSupport {

    private String key;
    private String arg1;
    private String arg2;

    public void setKey(String key) {
        this.key = key;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    @Override
    public int doStartTag() throws JspException {
        final Localization localization = WebApplicationContextUtils.getRequiredWebApplicationContext(
                pageContext.getServletContext()).getBean(WebRequestContext.class).getLocalization();
        if (localization == null) {
            throw new IllegalStateException("Localization is not available. Please make sure that the " +
                    "LocalizationResolverInterceptor is registered.");
        }

        String resource = localization.getResource(key);
        if (!Strings.isNullOrEmpty(arg1)) {
            if (!Strings.isNullOrEmpty(arg2)) {
                resource = MessageFormat.format(resource, arg1, arg2);
            } else {
                resource = MessageFormat.format(resource, arg1);
            }
        }

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

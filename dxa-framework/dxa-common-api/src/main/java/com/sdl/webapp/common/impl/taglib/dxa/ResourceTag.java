package com.sdl.webapp.common.impl.taglib.dxa;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationNotFoundException;
import lombok.Setter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.text.MessageFormat;

import static com.sdl.webapp.common.util.ApplicationContextHolder.getContext;

@Setter
public class ResourceTag extends TagSupport {

    private String key;

    private String arg1;

    private String arg2;

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {
        final Localization localization = getContext().getBean(WebRequestContext.class).getLocalization();
        if (localization == null) {
            throw new LocalizationNotFoundException("Localization is not available.");
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

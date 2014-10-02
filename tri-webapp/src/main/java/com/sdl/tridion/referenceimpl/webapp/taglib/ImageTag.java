package com.sdl.tridion.referenceimpl.webapp.taglib;

import com.sdl.tridion.referenceimpl.common.MediaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class ImageTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ImageTag.class);

    private String url;
    private String altText;
    private String widthFactor;
    private double aspect;
    private String cssClass;
    private int containerSize;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAlt(String altText) {
        this.altText = altText;
    }

    public void setWidthFactor(String widthFactor) {
        this.widthFactor = widthFactor;
    }

    public void setAspect(double aspect) {
        this.aspect = aspect;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public void setContainerSize(int containerSize) {
        this.containerSize = containerSize;
    }

    @Override
    public int doStartTag() throws JspException {
        final MediaHelper mediaHelper = WebApplicationContextUtils.getRequiredWebApplicationContext(
                pageContext.getServletContext()).getBean(MediaHelper.class);

        // TODO: Use MediaHelper to get the correct image URL

        final JspWriter out = pageContext.getOut();
        try {
            // TODO: Output <img> tag with the relevant attributes
            out.print("<img src=\"...\" alt=\"...\">");
        } catch (IOException e) {
            throw new JspException(e);
        }

        return SKIP_BODY;
    }
}

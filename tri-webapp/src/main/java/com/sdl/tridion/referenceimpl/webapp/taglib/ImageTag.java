package com.sdl.tridion.referenceimpl.webapp.taglib;

import com.google.common.base.Strings;
import com.sdl.tridion.referenceimpl.common.MediaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class ImageTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ImageTag.class);

    private static final String CONTEXTUAL_MEDIA_HELPER = "contextualMediaHelper";

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

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
        final MediaHelper mediaHelper = getMediaHelper();

        final String contextPath = URL_PATH_HELPER.getContextPath((HttpServletRequest) pageContext.getRequest());
        final String imageUrl = contextPath + mediaHelper.getResponsiveImageUrl(url, widthFactor, aspect, containerSize);
        LOG.debug("imageUrl={}", imageUrl);

        String imgWidth = widthFactor;
        if (Strings.isNullOrEmpty(widthFactor)) {
            imgWidth = mediaHelper.getDefaultMediaFill();
        }

        final JspWriter out = pageContext.getOut();
        try {
            out.write("<img");
            out.write(" src=\"" + imageUrl + "\"");

            if (!Strings.isNullOrEmpty(imgWidth)) {
                out.write(" width=\"" + imgWidth + "\"");
            }

            if (!Strings.isNullOrEmpty(altText)) {
                out.write(" alt=\"" + altText + "\"");
            }

            out.write(" data-aspect=\"" + String.format(Locale.US, "%.2f", aspect) + "\"");

            if (!Strings.isNullOrEmpty(cssClass)) {
                out.write(" class=\"" + cssClass + "\"");
            }

            out.write(">");
        } catch (IOException e) {
            throw new JspException(e);
        }

        return SKIP_BODY;
    }

    private MediaHelper getMediaHelper() {
        final WebApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(
                pageContext.getServletContext());

        // Try to get contextualMediaHelper if it exists
        final Map<String, MediaHelper> map = springContext.getBeansOfType(MediaHelper.class);
        if (map.containsKey(CONTEXTUAL_MEDIA_HELPER)) {
            return map.get(CONTEXTUAL_MEDIA_HELPER);
        }

        // Otherwise it is expected that there is exactly one MediaHelper; get it
        return springContext.getBean(MediaHelper.class);
    }
}

package com.sdl.webapp.main.taglib;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.main.markup.html.HtmlElement;
import com.sdl.webapp.main.markup.html.builders.HtmlBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class ImageTag extends HtmlElementTag {
    private static final Logger LOG = LoggerFactory.getLogger(ImageTag.class);

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
    public HtmlElement generateElement() {
        final MediaHelper mediaHelper = WebApplicationContextUtils.getRequiredWebApplicationContext(
                pageContext.getServletContext()).getBean(MediaHelper.class);

        final double imageAspect = aspect == 0.0 ? mediaHelper.getDefaultMediaAspect() : aspect;

        final String contextPath = URL_PATH_HELPER.getContextPath((HttpServletRequest) pageContext.getRequest());
        final String imageUrl = contextPath + mediaHelper.getResponsiveImageUrl(url, widthFactor, imageAspect,
                containerSize);
        LOG.debug("imageUrl={}", imageUrl);

        String imgWidth = widthFactor;
        if (Strings.isNullOrEmpty(widthFactor)) {
            imgWidth = mediaHelper.getDefaultMediaFill();
        }

        return HtmlBuilders.img(imageUrl)
                .withAlt(altText)
                .withWidth(imgWidth)
                .withClass(cssClass)
                .withAttribute("data-aspect", String.format(Locale.US, "%.2f", aspect))
                .build();
    }
}

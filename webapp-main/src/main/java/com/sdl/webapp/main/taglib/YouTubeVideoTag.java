package com.sdl.webapp.main.taglib;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.MediaHelper;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.UUID;

public class YouTubeVideoTag extends TagSupport {

    private String youTubeId;
    private String url;
    private String headline;
    private String widthFactor;
    private double aspect;
    private String cssClass;
    private int containerSize;

    public void setYouTubeId(String youTubeId) {
        this.youTubeId = youTubeId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
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

        JspWriter out = pageContext.getOut();
        try {
            if (!Strings.isNullOrEmpty(url)) {
                out.write(getYouTubePlaceholder());
            } else {
                out.write(getYouTubeEmbed());
            }
        } catch (IOException e) {
            throw new JspException(e);
        }

        return SKIP_BODY;
    }

    private String getYouTubePlaceholder() {
        final MediaHelper mediaHelper = WebApplicationContextUtils.getRequiredWebApplicationContext(
                pageContext.getServletContext()).getBean(MediaHelper.class);

        final double imageAspect = aspect == 0.0 ? mediaHelper.getDefaultMediaAspect() : aspect;

        final String placeholderImageUrl = mediaHelper.getResponsiveImageUrl(url, widthFactor, imageAspect,
                containerSize);

        return new StringBuilder().append("<div class=\"embed-video\">")
                .append("<img src=\"").append(placeholderImageUrl).append("\" alt=\"").append(headline).append("\">")
                .append("<button type=\"button\" data-video=\"").append(youTubeId).append("\" class=\"")
                .append(cssClass).append("\"><i class=\"fa fa-play-circle\"></i></button>").append("</div>").toString();
    }

    private String getYouTubeEmbed() {
        return new StringBuilder().append("<iframe src=\"https://www.youtube.com/embed/").append(youTubeId)
                .append("?version=3&enablejsapi=1\" id=\"").append("video" + UUID.randomUUID().toString())
                .append("\" allowfullscreen=\"true\" frameborder=\"0\" class=\"").append(cssClass)
                .append("\"></iframe>").toString();
    }
}

package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import com.sdl.webapp.common.markup.html.builders.SimpleElementBuilder;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

@SemanticEntity(entityName = "DataDownload", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class Download extends MediaItem {

    private static final Logger LOG = LoggerFactory.getLogger(Download.class);

    @SemanticProperties({
            @SemanticProperty("s:name"),
            @SemanticProperty("s:description")
    })
    @JsonProperty("Description")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Download{" +
                "url='" + getUrl() + '\'' +
                ", fileName='" + getFileName() + '\'' +
                ", fileSize=" + getFileSize() +
                ", mimeType='" + getMimeType() + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public String toHtml(String widthFactor) {
        return toHtml(widthFactor, 0, "", 0);
    }

    @Override
    public String toHtml(String widthFactor, double aspect, String cssClass, int containerSize) {
        String descriptionHtml = Strings.isNullOrEmpty(getDescription()) ? null : String.format("<small>%s</small>", getDescription());
        String s = new StringBuilder()
                .append("<div class=\"download-list\">")
                .append(String.format("<i class=\"fa %s\"></i>", this.getIconClass()))
                .append("<div>")
                .append(String.format("<a href=\"%s\">%s</a> <small class=\"size\">(%s)</small>", this.getUrl(), this.getFileName(), this.getFriendlyFileSize()))
                .append(String.format("%s", descriptionHtml))
                .append("</div>")
                .append("</div>").toString();
        return s;
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) {
        if (Strings.isNullOrEmpty(this.getUrl())) {
            LOG.warn("Skipping download with empty URL: {}", this);
            return null;
        }

        // TODO: this does not contain any XPM markup
        final SimpleElementBuilder innerDivBuilder = HtmlBuilders.div()
                .withContent(HtmlBuilders.a(this.getUrl())
                        .withContent(this.getFileName())
                        .build())
                .withContent(HtmlBuilders.element("small")
                        .withClass("size")
                        .withContent("(" + this.getFriendlyFileSize() + ")")
                        .build());

        if (!Strings.isNullOrEmpty(this.getDescription())) {
            innerDivBuilder.withContent(HtmlBuilders.element("small")
                    .withContent(this.getDescription())
                    .build());
        }

        return HtmlBuilders.div()
                .withClass("download-list")
                .withContent(HtmlBuilders.i().withClass("fa " + this.getIconClass()).build())
                .withContent(innerDivBuilder.build())
                .build();
    }

    @Override
    public void readFromXhtmlElement(Node xhtmlElement) {
        super.readFromXhtmlElement(xhtmlElement);

        try {
            this.setMvcData(new MediaItemMvcData("Core:Entity:Image"));
        } catch (DxaException e) {

        }
    }
}

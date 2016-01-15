package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.a;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.div;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.empty;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.i;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.small;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;

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
    public HtmlElement toHtmlElement(String widthFactor) throws DxaException {
        return toHtmlElement(widthFactor, 0, "", 0);
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize) throws DxaException {
        return toHtmlElement(widthFactor, aspect, cssClass, containerSize, "");
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) throws DxaException {
        if (isEmpty(getUrl())) {
            LOG.warn("Skipping download with empty URL: {}", this);
            throw new DxaException("URL is null for download component: " + this);
        }

        // TODO: this does not contain any XPM markup
        return div().withClass("download-list")
                .withNode(
                        i().withClass("fa").withClass(getIconClass()).build()
                ).withNode(
                        div().withNode(
                                a(getUrl()).withTextualContent(getFileName()).build()
                        ).withNode(
                                small().withClass("size").withTextualContent(format("(%s)", getFriendlyFileSize())).build()
                        ).withNode(
                                (isEmpty(getDescription()) ? empty() : small().withTextualContent(getDescription())).build()
                        ).build()
                ).build();
    }

    @Override
    public void readFromXhtmlElement(Node xhtmlElement) {
        super.readFromXhtmlElement(xhtmlElement);
        this.setMvcData(getMvcData());
    }

    @Override
    public MvcData getMvcData() {
        return MvcDataCreator.creator()
                .fromQualifiedName("Core:Entity:Download")
                .defaults(DefaultsMvcData.CORE_ENTITY)
                .create();
    }
}

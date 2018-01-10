package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * @dxa.publicApi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@SemanticEntity(entityName = "MediaObject", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public abstract class MediaItem extends AbstractEntityModel {

    private static final ImmutableMap<String, String> MIME_TYPE_TO_ICON_CLASS_MAPPING =
            ImmutableMap.<String, String>builder()
                    .put("application/ms-excel", "excel")
                    .put("application/pdf", "pdf")
                    .put("application/x-wav", "audio")
                    .put("audio/x-mpeg", "audio")
                    .put("application/msword", "word")
                    .put("text/rtf", "word")
                    .put("application/zip", "archive")
                    .put("image/gif", "image")
                    .put("image/jpeg", "image")
                    .put("image/png", "image")
                    .put("image/x-bmp", "image")
                    .put("text/plain", "text")
                    .put("text/css", "code")
                    .put("application/x-javascript", "code")
                    .put("application/ms-powerpoint", "powerpoint")
                    .put("video/vnd.rn-realmedia", "video")
                    .put("video/quicktime", "video")
                    .put("video/mpeg", "video")
                    .build();

    private MediaHelper mediaHelper;

    @SemanticProperty("s:contentUrl")
    @JsonProperty("Url")
    private String url;

    @JsonProperty("IsEmbedded")
    private boolean isEmbedded;

    @JsonProperty("FileName")
    private String fileName;

    @SemanticProperty("s:contentSize")
    @JsonProperty("FileSize")
    private long fileSize;

    @JsonProperty("MimeType")
    private String mimeType;

    @Nullable
    protected static String getNodeAttribute(@NonNull Node xhtmlElement, @NonNull String key) {
        NamedNodeMap attributes = xhtmlElement.getAttributes();
        if (attributes == null) {
            log.trace("XHTML Node {} attributes object is null", xhtmlElement);
            return null;
        }

        Node node = attributes.getNamedItem(key);
        return node != null ? node.getNodeValue() : null;
    }

    protected MediaHelper getMediaHelper() {
        if (this.mediaHelper == null) {
            this.mediaHelper = ApplicationContextHolder.getContext().getBean(MediaHelper.MediaHelperFactory.class)
                    .getMediaHelperInstance();
        }

        return this.mediaHelper;
    }

    @JsonIgnore
    public boolean isImage() {
        return false;
    }

    @JsonIgnore
    public String getIconClass() {
        String fileType = MIME_TYPE_TO_ICON_CLASS_MAPPING.get(this.getMimeType());
        return fileType != null ? String.format("fa-file-%s-o", fileType) : "fa-file";
    }

    @JsonIgnore
    public String getFriendlyFileSize() {
        String[] sizes = {"B", "KB", "MB", "GB", "TB", "PB", "EB"};
        double len = fileSize;
        int order = 0;
        while (len >= 1024 && order + 1 < sizes.length) {
            order++;
            len = len / 1024;
        }

        return String.format("%s %s", Math.ceil(len), sizes[order]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlElement toHtmlElement() throws DxaException {
        return this.toHtmlElement("100%");
    }

    /**
     * Renders an HTML representation of the Item.
     *
     * @param widthFactor The factor to apply to the width - can be % (eg "100%") or absolute (eg "120")
     * @return The HTML element representation
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    public abstract HtmlElement toHtmlElement(String widthFactor) throws DxaException;

    /**
     * Renders an HTML representation of the Item.
     *
     * @param widthFactor   The factor to apply to the width - can be % (eg "100%") or absolute (eg "120")
     * @param aspect        The aspect ratio to apply
     * @param cssClass      Optional CSS class name(s) to apply
     * @param containerSize The size (in grid column units) of the containing element
     * @return The HTML element representation
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    public abstract HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize) throws DxaException;

    /**
     * Renders an HTML representation of the Item.
     *
     * @param widthFactor   The factor to apply to the width - can be % (eg "100%") or absolute (eg "120")
     * @param aspect        The aspect ratio to apply
     * @param cssClass      Optional CSS class name(s) to apply
     * @param containerSize The size (in grid column units) of the containing element
     * @param contextPath   Context path to prepend the urls
     * @return The HTML element representation
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    public abstract HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) throws DxaException;

    public void readFromXhtmlElement(Node xhtmlElement) {
        // Return the Item (Reference) ID part of the TCM URI.
        String id = getNodeAttribute(xhtmlElement, "xlink:href");

        Assert.notNull(id);
        Assert.isTrue(id.contains("-"));

        setId(id.split("-")[1]);

        setEmbedded(true);

        setUrl(getNodeAttribute(xhtmlElement, "src"));
        setHtmlClasses(getNodeAttribute(xhtmlElement, "class"));
        setFileName(getNodeAttribute(xhtmlElement, "data-multimediaFileName"));
        setMimeType(getNodeAttribute(xhtmlElement, "data-multimediaMimeType"));

        String fileSize = getNodeAttribute(xhtmlElement, "data-multimediaFileSize");
        if (!Strings.isNullOrEmpty(fileSize)) {
            setFileSize(Integer.parseInt(fileSize));
        }
    }
}

package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import org.w3c.dom.Node;

import java.util.HashMap;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;

@SemanticEntity(entityName = "MediaObject", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public abstract class MediaItem extends AbstractEntityModel {

    private static final HashMap<String, String> MIME_TYPE_TO_ICON_CLASS_MAPPING;

    static {
        MIME_TYPE_TO_ICON_CLASS_MAPPING = new HashMap<>();

        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("application/ms-excel", "excel");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("application/pdf", "pdf");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("application/x-wav", "audio");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("audio/x-mpeg", "audio");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("application/msword", "word");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("text/rtf", "word");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("application/zip", "archive");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("image/gif", "image");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("image/jpeg", "image");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("image/png", "image");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("image/x-bmp", "image");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("text/plain", "text");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("text/css", "code");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("application/x-javascript", "code");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("application/ms-powerpoint", "powerpoint");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("video/vnd.rn-realmedia", "video");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("video/quicktime", "video");
        MIME_TYPE_TO_ICON_CLASS_MAPPING.put("video/mpeg", "video");
    }

    @SemanticProperty("s:contentUrl")
    @JsonProperty("Url")
    private String url;

    @JsonProperty("IsEmbedded")
    private Boolean isEmbedded;

    @JsonProperty("FileName")
    private String fileName;

    @SemanticProperty("s:contentSize")
    @JsonProperty("FileSize")
    private int fileSize;

    @JsonProperty("MimeType")
    private String mimeType;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getIsEmbedded() {
        return this.isEmbedded;
    }

    public void setIsEmbedded(Boolean isEmbedded) {
        this.isEmbedded = isEmbedded;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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
        double len = getFileSize();
        int order = 0;
        while (len >= 1024 && order + 1 < sizes.length) {
            order++;
            len = len / 1024;
        }

        return String.format("%s %s", Math.ceil(len), sizes[order]);
    }

    @Override
    public HtmlElement toHtmlElement() throws DxaException {
        return this.toHtmlElement("100%");
    }

    /**
     * Renders an HTML representation of the Item.
     *
     * @param widthFactor The factor to apply to the width - can be % (eg "100%") or absolute (eg "120")
     * @return The HTML element representation
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
     */
    public abstract HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) throws DxaException;

    /**
     * Read properties from XHTML element.
     */
    public void readFromXhtmlElement(Node xhtmlElement) {
        // Return the Item (Reference) ID part of the TCM URI.
        this.setId(xhtmlElement.getAttributes().getNamedItem("xlink:href").getNodeValue().split("-")[1]);
        this.setUrl(xhtmlElement.getAttributes().getNamedItem("src").getNodeValue());
        this.setFileName(xhtmlElement.getAttributes().getNamedItem("data-multimediaFileName").getNodeValue());
        String fileSize = xhtmlElement.getAttributes().getNamedItem("data-multimediaFileSize").getNodeValue();
        if (!Strings.isNullOrEmpty(fileSize)) {
            this.setFileSize(Integer.parseInt(fileSize));
        }
        this.setMimeType(xhtmlElement.getAttributes().getNamedItem("data-multimediaMimeType").getNodeValue());
        this.setIsEmbedded(true);
    }
}

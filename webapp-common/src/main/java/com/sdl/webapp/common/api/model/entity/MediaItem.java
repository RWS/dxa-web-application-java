package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.SemanticEntity;
import com.sdl.webapp.common.api.mapping.SemanticProperty;
import com.sdl.webapp.common.api.mapping.Vocabularies;

@SemanticEntity(entityName = "MediaObject", vocab = Vocabularies.SCHEMA_ORG, prefix = "s", pub = true)
public abstract class MediaItem extends EntityBase {

    @SemanticProperty("s:contentUrl")
    private String url;

    private String fileName;

    @SemanticProperty("s:contentSize")
    private int fileSize;

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
}

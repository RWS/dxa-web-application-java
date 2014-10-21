package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.Binary;
import org.dd4t.contentmodel.BinaryData;
import org.dd4t.contentmodel.Schema;

import org.joda.time.DateTime;

public class BinaryImpl extends BaseRepositoryLocalItem implements Binary {

	@JsonProperty("Schema") @JsonDeserialize(as = SchemaImpl.class)
    private Schema schema;

    private int height;
    private int width;
    private String alt;

    @JsonProperty("MimeType")
    private String mimeType = null;

    private BinaryData binaryData;

	@JsonProperty("LastPublishDate") @JsonDeserialize(as = DateTime.class)
    private DateTime lastPublishDate = new DateTime(0);

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    @Override
    public BinaryData getBinaryData() {
        return this.binaryData;
    }

    @Override
    public void setBinaryData(BinaryData binaryData) {
        this.binaryData = binaryData;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String getAlt() {
        return alt;
    }

    @Override
    public void setAlt(String alt) {
        this.alt = alt;
    }

    @Override
    public String getMimeType() {
        return this.mimeType;
    }

    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public DateTime getLastPublishDate() {
        return this.lastPublishDate;
    }

    @Override
    public void setLastPublishDate(final DateTime date) {
        this.lastPublishDate = date;
    }
}
package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.Category;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.HasContent;
import org.dd4t.contentmodel.HasMetadata;
import org.dd4t.contentmodel.HasMultimedia;
import org.dd4t.contentmodel.Multimedia;
import org.dd4t.core.util.DateUtils;

import org.joda.time.DateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentImpl extends BaseComponent implements GenericComponent, HasContent, HasMetadata, HasMultimedia {

	@JsonProperty("ComponentType") @JsonDeserialize(as = ComponentImpl.ComponentType.class)
    protected ComponentType componentType;

    @JsonProperty("LastPublishedDate")
    protected String lastPublishedDateAsString;

    @JsonProperty("RevisionDate")
    protected String revisionDateAsString;

    @JsonProperty("Version")
    protected int version;

	@JsonProperty("MetadataFields") @JsonDeserialize(contentAs = BaseField.class)
    private Map<String, Field> metadata;

	@JsonProperty("Fields") @JsonDeserialize(contentAs = BaseField.class)
    private Map<String, Field> content;

	@JsonProperty("Multimedia") @JsonDeserialize(as = MultimediaImpl.class)
    private Multimedia multimedia;

	@JsonProperty("Categories") @JsonDeserialize(contentAs = CategoryImpl.class)
    private List<Category> categories;

    @Override
    public DateTime getLastPublishedDate() {
        if (lastPublishedDateAsString == null || lastPublishedDateAsString.equals("")) {
            return new DateTime();
        }
        return DateUtils.convertStringToDate(lastPublishedDateAsString);
    }

    @Override
    public void setLastPublishedDate(DateTime date) {
        this.lastPublishedDateAsString = DateUtils.convertDateToString(date);
    }

    @Override
    public DateTime getRevisionDate() {
        if (revisionDateAsString == null || revisionDateAsString.equals("")) {
            return new DateTime();
        }
        return DateUtils.convertStringToDate(revisionDateAsString);
    }

    @Override
    public void setRevisionDate(DateTime date) {
        this.revisionDateAsString = DateUtils.convertDateToString(date);
    }

    public int getVersion() {

        return version;
    }

    public void setVersion(int version) {

        this.version = version;
    }

    /**
     * Get the metadata
     *
     * @return a map of field objects representing the metadata
     */
    public Map<String, Field> getMetadata() {
        if (metadata == null) {
            metadata = new HashMap<String, Field>();
        }
        return metadata;
    }

    /**
     * Set the metadata
     */
    public void setMetadata(Map<String, Field> metadata) {
        this.metadata = metadata;
    }

    /**
     * Get the content
     *
     * @return a map of field objects representing the content
     */
    public Map<String, Field> getContent() {
        if (content == null) {
            content = new HashMap<String, Field>();
        }
        return content;
    }

    /**
     * Set the content
     */
    public void setContent(Map<String, Field> content) {
        this.content = content;
    }

    /**
     * Get the component type
     *
     * @return the component type
     */
    public ComponentType getComponentType() {
        return componentType;
    }

    /**
     * Set the component type
     *
     * @param componentType
     */
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    /**
     * Get the multimedia object
     *
     * @return the multimedia object
     */
    @Override
    public Multimedia getMultimedia() {
        return multimedia;
    }

    /**
     * Set the multimedia object
     */
    @Override
    public void setMultimedia(Multimedia multimedia) {
        this.multimedia = multimedia;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
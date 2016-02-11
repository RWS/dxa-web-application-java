package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import org.joda.time.DateTime;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * <p>Article class.</p>
 */
@SemanticEntity(entityName = "Article", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class Article extends AbstractEntityModel {

    @JsonProperty("Headline")
    @SemanticProperty("s:headline")
    private String headline;

    @JsonProperty("Image")
    @SemanticProperty("s:image")
    private Image image;

    @JsonProperty("Date")
    @SemanticProperty("s:dateCreated")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private DateTime date;

    @JsonProperty("Description")
    @SemanticProperty("s:about")
    private String description;

    @JsonProperty("ArticleBody")
    @SemanticProperty("s:articleBody")
    private List<Paragraph> articleBody;

    /**
     * <p>Getter for the field <code>headline</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * <p>Setter for the field <code>headline</code>.</p>
     *
     * @param headline a {@link java.lang.String} object.
     */
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    /**
     * <p>Getter for the field <code>image</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.entity.Image} object.
     */
    public Image getImage() {
        return image;
    }

    /**
     * <p>Setter for the field <code>image</code>.</p>
     *
     * @param image a {@link com.sdl.webapp.common.api.model.entity.Image} object.
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * <p>Getter for the field <code>date</code>.</p>
     *
     * @return a {@link org.joda.time.DateTime} object.
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * <p>Setter for the field <code>date</code>.</p>
     *
     * @param date a {@link org.joda.time.DateTime} object.
     */
    public void setDate(DateTime date) {
        this.date = date;
    }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>articleBody</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Paragraph> getArticleBody() {
        return articleBody;
    }

    /**
     * <p>Setter for the field <code>articleBody</code>.</p>
     *
     * @param articleBody a {@link java.util.List} object.
     */
    public void setArticleBody(List<Paragraph> articleBody) {
        this.articleBody = articleBody;
    }
}

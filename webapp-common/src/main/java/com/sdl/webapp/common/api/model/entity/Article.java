package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.SemanticEntity;
import com.sdl.webapp.common.api.mapping.SemanticProperty;
import com.sdl.webapp.common.api.mapping.Vocabularies;

import java.util.Date;
import java.util.List;

@SemanticEntity(entityName = "Article", vocab = Vocabularies.SCHEMA_ORG, prefix = "s", pub = true)
public class Article extends EntityBase {

    @SemanticProperty("s:headline")
    private String headline;

    @SemanticProperty("s:image")
    private Image image;

    @SemanticProperty("s:dateCreated")
    private Date date;

    @SemanticProperty("s:about")
    private String description;

    @SemanticProperty("s:articleBody")
    private List<Paragraph> articleBody;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Paragraph> getArticleBody() {
        return articleBody;
    }

    public void setArticleBody(List<Paragraph> articleBody) {
        this.articleBody = articleBody;
    }
}

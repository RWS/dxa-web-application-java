package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;

@SemanticEntity(entityName = "Article", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
@Data
@EqualsAndHashCode(callSuper = true)
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
}

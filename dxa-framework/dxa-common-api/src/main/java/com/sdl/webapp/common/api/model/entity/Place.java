package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.RichText;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;

@SemanticEntity(entityName = "Place", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class Place extends AbstractEntityModel {

    @JsonProperty("Name")
    private String name;

    @SemanticProperty("s:image")
    @JsonProperty("Image")
    private Image image;

    @SemanticProperty("s:address")
    @JsonProperty("Address")
    private RichText address;

    @SemanticProperty("s:telephone")
    @JsonProperty("Telephone")
    private String telephone;

    @SemanticProperty("s:faxNumber")
    @JsonProperty("FaxNumber")
    private String faxNumber;

    @SemanticProperty("s:email")
    @JsonProperty("Email")
    private String email;

    @SemanticProperty("s:geo")
    @JsonProperty("Location")
    private Location location;
}

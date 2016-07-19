package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.model.RichText;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Paragraph extends AbstractEntityModel {

    @JsonProperty("Subheading")
    private String subheading;

    @JsonProperty("Content")
    private RichText content;

    @JsonProperty("Media")
    private MediaItem media;

    @JsonProperty("Caption")
    private String caption;
}

package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class Tag {

    @JsonProperty("DisplayText")
    private String displayText;

    @JsonProperty("Key")
    private String key;

    @JsonProperty("TagCategory")
    private String tagCategory;
}

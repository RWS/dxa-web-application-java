package com.sdl.webapp.common.impl.localization.semantics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JsonFieldSemantics {

    @JsonProperty("Prefix")
    private String prefix;

    @JsonProperty("Entity")
    private String entity;

    @JsonProperty("Property")
    private String property;
}

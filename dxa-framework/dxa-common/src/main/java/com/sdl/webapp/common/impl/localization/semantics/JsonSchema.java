package com.sdl.webapp.common.impl.localization.semantics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class JsonSchema {

    @JsonProperty("Id")
    private long id;

    @JsonProperty("RootElement")
    private String rootElement;

    @JsonProperty("Fields")
    private List<JsonSchemaField> fields;

    @JsonProperty("Semantics")
    private List<JsonSchemaSemantics> semantics;
}

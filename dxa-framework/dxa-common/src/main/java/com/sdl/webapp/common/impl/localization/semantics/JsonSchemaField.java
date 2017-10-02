package com.sdl.webapp.common.impl.localization.semantics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class JsonSchemaField {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Path")
    private String path;

    @JsonProperty("IsMultiValue")
    private boolean isMultiValue;

    @JsonProperty("Semantics")
    private List<JsonFieldSemantics> semantics;

    @JsonProperty("Fields")
    private List<JsonSchemaField> fields;

    @JsonProperty("FieldType")
    private JsonSchemaFieldType fieldType;

    @JsonProperty("RootElementName")
    private String rootElementName;
}

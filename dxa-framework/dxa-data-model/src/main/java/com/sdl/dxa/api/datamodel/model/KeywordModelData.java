package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@JsonTypeName
public class KeywordModelData extends ViewModelData {

    private String id;

    private String description;

    private String key;

    private String taxonomyId;

    private String title;
}

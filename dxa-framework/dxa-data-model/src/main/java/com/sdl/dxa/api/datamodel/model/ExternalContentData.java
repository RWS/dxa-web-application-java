package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Value;

@Value
@JsonTypeName
public class ExternalContentData {

    private String displayTypeId;

    private String id;

    private String templateFragment;

    private ContentModelData metadata;
}

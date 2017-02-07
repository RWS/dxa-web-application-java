package com.sdl.dxa.api.model.data;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Value;

@Value
@JsonTypeName
public class ExternalContentData {

    private String displayTypeId;

    private String id;

    private ContentModelData metadata;
}

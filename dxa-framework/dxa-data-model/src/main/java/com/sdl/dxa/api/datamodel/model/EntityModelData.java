package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@JsonTypeName
public class EntityModelData extends ViewModelData {

    private String id;

    private String linkUrl;

    private ContentModelData content;

    private BinaryContentData binaryContent;

    private ExternalContentData externalContent;
}

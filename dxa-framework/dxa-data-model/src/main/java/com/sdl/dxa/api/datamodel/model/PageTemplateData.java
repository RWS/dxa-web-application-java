package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Value;
import org.joda.time.DateTime;

@Value
@JsonTypeName
public class PageTemplateData {

    private String id;

    private String title;

    private String fileExtension;

    private DateTime revisionDate;

    private ContentModelData metadata;
}

package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;

@NoArgsConstructor
@Data
@Accessors(chain = true)
@JsonTypeName
public class ComponentTemplateData {

    private String id;

    private String title;

    private DateTime revisionDate;

    private String outputFormat;

    private ContentModelData metadata;
}

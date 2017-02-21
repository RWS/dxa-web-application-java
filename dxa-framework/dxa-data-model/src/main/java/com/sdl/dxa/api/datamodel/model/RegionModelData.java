package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@JsonTypeName
public class RegionModelData extends ViewModelData {

    private String name;

    private String includePageUrl;

    private List<EntityModelData> entities;

    private List<RegionModelData> regions;
}

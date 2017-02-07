package com.sdl.dxa.api.model.data;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
@JsonTypeName
public class PageModelData extends ViewModelData {

    private String id;

    private Map<String, String> meta;

    private String title;

    private List<RegionModelData> regions;
}

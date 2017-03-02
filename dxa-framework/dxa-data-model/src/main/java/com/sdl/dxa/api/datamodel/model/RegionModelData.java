package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
@JsonTypeName
public class RegionModelData extends ViewModelData {

    private String name;

    private String includePageUrl;

    private List<EntityModelData> entities;

    private List<RegionModelData> regions;

    @Builder
    public RegionModelData(String schemaId, String htmlClasses, Map<String, Object> xpmMetadata, ContentModelData metadata, Map<String, Object> extensionData, MvcModelData mvcData, String name, String includePageUrl, List<EntityModelData> entities, List<RegionModelData> regions) { // NOSONAR
        super(schemaId, htmlClasses, xpmMetadata, metadata, extensionData, mvcData);
        this.name = name;
        this.includePageUrl = includePageUrl;
        this.entities = entities;
        this.regions = regions;
    }

    @Builder
    public RegionModelData(String name, String includePageUrl, List<EntityModelData> entities, List<RegionModelData> regions) {
        this.name = name;
        this.includePageUrl = includePageUrl;
        this.entities = entities;
        this.regions = regions;
    }
}

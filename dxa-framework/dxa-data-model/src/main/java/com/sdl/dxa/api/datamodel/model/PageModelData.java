package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sdl.dxa.api.datamodel.model.util.CanWrapData;
import com.sdl.dxa.api.datamodel.model.util.ModelDataWrapper;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
@JsonTypeName
public class PageModelData extends ViewModelData implements CanWrapData {

    private String id;

    private Map<String, String> meta;

    private String title;

    private List<RegionModelData> regions;

    @Override
    public ModelDataWrapper getDataWrapper() {
        return new ModelDataWrapper() {
            @Override
            public ContentModelData getMetadata() {
                return PageModelData.this.getMetadata();
            }

            @Override
            public Object getWrappedModel() {
                return PageModelData.this;
            }
        };
    }
}

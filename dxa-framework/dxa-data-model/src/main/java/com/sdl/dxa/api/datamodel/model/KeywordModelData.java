package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sdl.dxa.api.datamodel.model.util.CanWrapData;
import com.sdl.dxa.api.datamodel.model.util.ModelDataWrapper;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@JsonTypeName
public class KeywordModelData extends ViewModelData implements CanWrapData {

    private String id;

    private String description;

    private String key;

    private String taxonomyId;

    private String title;

    @Override
    public ModelDataWrapper getDataWrapper() {
        return new ModelDataWrapper() {
            @Override
            public ContentModelData getMetadata() {
                return KeywordModelData.this.getMetadata();
            }

            @Override
            public Object getWrappedModel() {
                return KeywordModelData.this;
            }
        };
    }
}

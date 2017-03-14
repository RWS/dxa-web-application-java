package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sdl.dxa.api.datamodel.model.util.CanWrapContentAndMetadata;
import com.sdl.dxa.api.datamodel.model.util.ModelDataWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@JsonTypeName
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class KeywordModelData extends ViewModelData implements CanWrapContentAndMetadata {

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

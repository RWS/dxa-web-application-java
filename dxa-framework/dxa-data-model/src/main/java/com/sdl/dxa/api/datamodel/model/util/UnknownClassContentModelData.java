package com.sdl.dxa.api.datamodel.model.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sdl.dxa.api.datamodel.model.ContentModelData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/**
 * Handler of an unknown entity-level class. Basically represents any data structure that is not known with a Map.
 */
@EqualsAndHashCode(callSuper = true)
public class UnknownClassContentModelData extends ContentModelData implements HandlesOwnTypeInformation {

    @Getter
    @JsonIgnore
    private String typeId;

    @Override
    protected boolean isRemoveDollarType(@Nullable Object typeId) {
        this.typeId = String.valueOf(typeId);
        return true;
    }
}

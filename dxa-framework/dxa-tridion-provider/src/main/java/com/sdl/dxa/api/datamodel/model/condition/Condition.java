package com.sdl.dxa.api.datamodel.model.condition;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sdl.dxa.api.datamodel.json.Polymorphic;
import com.sdl.dxa.api.datamodel.model.util.HandlesHierarchyTypeInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Polymorphic
@JsonTypeName
public class Condition implements HandlesHierarchyTypeInformation {

    private boolean negate;

    @Override
    public String getTypeId() {
        return Condition.class.getSimpleName();
    }
}

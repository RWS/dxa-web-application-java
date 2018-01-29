package com.sdl.dxa.api.datamodel.model.known;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sdl.dxa.api.datamodel.json.Polymorphic;
import com.sdl.dxa.api.datamodel.model.util.HandlesTypeInformationForListWrapper;

@Polymorphic
@JsonTypeName("KnownParentClass")
public class KnownParentClass implements HandlesTypeInformationForListWrapper {

    @Override
    public String getTypeId() {
        return "KnownParentClass";
    }
}

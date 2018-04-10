package com.sdl.dxa.api.datamodel.model.known;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@JsonTypeName
@Data
public class FirstChildKnownClass extends KnownParentClass {

    private String id;
}

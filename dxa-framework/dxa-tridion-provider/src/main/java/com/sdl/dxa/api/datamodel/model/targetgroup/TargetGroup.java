package com.sdl.dxa.api.datamodel.model.targetgroup;

import com.sdl.dxa.api.datamodel.model.condition.Condition;
import lombok.Data;

import java.util.List;

@Data
public class TargetGroup {

    private String id;

    private String title;

    private String description;

    private List<Condition> conditions;
}

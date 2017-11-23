package com.sdl.dxa.api.datamodel.model.condition;

import com.sdl.dxa.api.datamodel.model.targetgroup.TargetGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TargetGroupCondition extends Condition {

    private TargetGroup targetGroup;
}

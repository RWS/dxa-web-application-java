package com.sdl.dxa.api.datamodel.model.condition;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sdl.dxa.api.datamodel.model.targetgroup.TargetGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@Accessors(chain = true)
@JsonTypeName
public class TargetGroupCondition extends Condition {

    private TargetGroup targetGroup;
}

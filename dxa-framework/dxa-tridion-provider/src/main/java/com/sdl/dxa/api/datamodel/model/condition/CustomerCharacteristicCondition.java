package com.sdl.dxa.api.datamodel.model.condition;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@Accessors(chain = true)
@JsonTypeName
public class CustomerCharacteristicCondition extends Condition {

    private Object value;

    private String name;

    private ConditionOperator operator;
}

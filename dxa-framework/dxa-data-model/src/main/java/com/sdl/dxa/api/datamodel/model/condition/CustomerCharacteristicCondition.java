package com.sdl.dxa.api.datamodel.model.condition;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerCharacteristicCondition extends Condition {

    private Object value;

    private String name;

    private ConditionOperator operator;
}

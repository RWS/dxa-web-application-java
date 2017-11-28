package com.sdl.dxa.api.datamodel.model.condition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ConditionOperator {
    UNKNOWN_BY_CLIENT(-2147483648),
    EQUALS(0),
    GREATER_THAN(1),
    LESS_THEN(2),
    NOT_EQUAL(3),
    STRING_EQUALS(4),
    CONTAINS(5),
    STARTS_WITH(6),
    ENDS_WITH(7);

    private final int index;

    ConditionOperator(int index) {
        this.index = index;
    }

    @JsonCreator
    public static ConditionOperator fromValue(final int typeCode) {
        for (ConditionOperator operator : ConditionOperator.values()) {
            if (operator.index == typeCode) {
                return operator;
            }
        }

        throw new IllegalArgumentException("Invalid ConditionalOperator type code: " + typeCode);
    }

    @JsonValue
    public int toValue() {
        return this.index;
    }
}

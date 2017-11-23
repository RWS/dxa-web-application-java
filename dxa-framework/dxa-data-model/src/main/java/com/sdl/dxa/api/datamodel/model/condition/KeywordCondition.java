package com.sdl.dxa.api.datamodel.model.condition;

import com.sdl.dxa.api.datamodel.model.KeywordModelData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KeywordCondition extends Condition {

    private KeywordModelData keywordModelData;

    private ConditionOperator operator;

    private Object value;
}

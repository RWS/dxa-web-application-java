package com.sdl.dxa.api.datamodel.model.condition;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sdl.dxa.api.datamodel.model.KeywordModelData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@Accessors(chain = true)
@JsonTypeName
public class KeywordCondition extends Condition {

    private KeywordModelData keywordModelData;

    private ConditionOperator operator;

    private Object value;
}

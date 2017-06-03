package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.dd4t.contentmodel.Condition;
import org.dd4t.contentmodel.ConditionOperator;

import java.io.Serializable;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public class ConditionImpl implements Condition, Serializable {

    private static final long serialVersionUID = 7601871582992096158L;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Negate")
    private boolean negate;

    @JsonProperty("Operator")
    private ConditionOperator operator;

    @JsonProperty("Value")
    private String value;

    @Override
    public String getName () {
        return name;
    }

    @Override
    public void setName (final String name) {
        this.name = name;
    }

    @Override
    public boolean isNegate () {
        return negate;
    }

    @Override
    public void setNegate (final boolean negate) {
        this.negate = negate;
    }

    @Override
    public ConditionOperator getOperator () {
        return operator;
    }

    @Override
    @JsonSetter("Operator")
    public void setOperator (final int operator) {
        this.operator = ConditionOperator.findByValue(operator);
    }

    @Override
    public String getValue () {
        return value;
    }

    @Override
    public void setValue (final String value) {
        this.value = value;
    }
}

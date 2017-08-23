package org.dd4t.contentmodel;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public enum ConditionOperator {

    EQUALS(0),
    GREATERTHAN(1),
    LESSTHAN(2),
    NOTEQUAL(3),
    STRINGEQUALS(4),
    CONTAINS(5),
    STARTSWITH(6),
    ENDSWITH(7),
    UNKNOWN(-1);

    private final int value;


    ConditionOperator (final int v) {
        this.value=v;
    }

    public static ConditionOperator findByValue (int value) {
        for (ConditionOperator operator : values()) {
            if (operator.getValue() == value) {
                return operator;
            }
        }

        return UNKNOWN;
    }

    public int getValue () {
        return value;
    }
}

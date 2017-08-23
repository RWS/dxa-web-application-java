package org.dd4t.contentmodel;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public interface Condition {

    String getName ();

    void setName (final String name);

    boolean isNegate ();

    void setNegate (final boolean negate);

    ConditionOperator getOperator ();

    void setOperator (final int operator);

    String getValue ();

    void setValue (final String value);
}

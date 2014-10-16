package org.dd4t.contentmodel.impl;

import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldType;

import java.util.LinkedList;
import java.util.List;

public class NumericField extends BaseField implements Field {

    public NumericField() {
        setFieldType(FieldType.Number);
    }

    @Override
    public List<Object> getValues() {
        List<Double> dblValues = getNumericValues();

        List<Object> l = new LinkedList<Object>();
        for (Double d : dblValues) {
            l.add(d);
        }

        return l;
    }
}

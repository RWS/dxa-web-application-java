package org.dd4t.contentmodel.impl;

import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.core.util.DateUtils;

import java.util.LinkedList;
import java.util.List;

public class DateField extends BaseField implements Field {

    public DateField() {
        setFieldType(FieldType.DATE);
    }

    @Override
    public List<Object> getValues() {
        List<String> dateValues = getDateTimeValues();
        List<Object> l = new LinkedList<Object>();

        for (String d : dateValues) {
            l.add(DateUtils.convertStringToDate(d));
        }

        return l;
    }
}

package com.sdl.webapp.dd4t.fieldconverters;

import com.google.common.base.Strings;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DateFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = { FieldType.DATE };

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass) throws FieldConverterException {
        final List<DateTime> dateTimeValues = new ArrayList<>();

        for (String value : field.getDateTimeValues()) {
            if (!Strings.isNullOrEmpty(value)) {
                dateTimeValues.add(new DateTime(value));
            }
        }

        return dateTimeValues;
    }
}

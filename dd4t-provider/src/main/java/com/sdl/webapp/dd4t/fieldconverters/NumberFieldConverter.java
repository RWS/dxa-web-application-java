package com.sdl.webapp.dd4t.fieldconverters;

import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NumberFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = { FieldType.NUMBER };

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass) throws FieldConverterException {
        final List<Double> numericValues = field.getNumericValues();

        if (targetClass.isAssignableFrom(Double.class)) {
            return numericValues;
        } else if (targetClass.isAssignableFrom(Integer.class)) {
            return getIntegerValues(numericValues);
        } else {
            throw new UnsupportedTargetTypeException(targetClass);
        }
    }

    private List<Integer> getIntegerValues(List<Double> numericValues) {
        final List<Integer> integerValues = new ArrayList<>();
        for (Double number : numericValues) {
            integerValues.add((int) Math.round(number));
        }
        return integerValues;
    }
}

package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.sdl.webapp.common.util.StringUtils.toStrings;

@Component
public class NumberFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = {FieldType.NUMBER};

    private static List<Integer> getIntegerValues(List<Double> numericValues) {
        final List<Integer> integerValues = new ArrayList<>();
        for (Double number : numericValues) {
            integerValues.add((int) Math.round(number));
        }
        return integerValues;
    }

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    public List<String> getStringValues(BaseField field) throws FieldConverterException {
        return toStrings(getFieldValues(field, Double.class));
    }

    @Override
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass, ModelBuilderPipeline builder) throws FieldConverterException {
        final List<Double> numericValues = field.getNumericValues();

        if (targetClass.isAssignableFrom(Double.class)) {
            return numericValues;
        } else if (targetClass.isAssignableFrom(Integer.class)) {
            return getIntegerValues(numericValues);
        } else {
            throw new UnsupportedTargetTypeException(targetClass);
        }
    }
}

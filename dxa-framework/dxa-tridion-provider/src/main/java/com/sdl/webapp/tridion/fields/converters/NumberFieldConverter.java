package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
/**
 * <p>NumberFieldConverter class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class NumberFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = {FieldType.NUMBER};

    private static List<Integer> getIntegerValues(List<Double> numericValues) {
        final List<Integer> integerValues = new ArrayList<>();
        for (Double number : numericValues) {
            integerValues.add((int) Math.round(number));
        }
        return integerValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    /**
     * {@inheritDoc}
     */
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

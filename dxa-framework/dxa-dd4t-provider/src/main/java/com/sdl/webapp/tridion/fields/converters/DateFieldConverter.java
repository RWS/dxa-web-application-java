package com.sdl.webapp.tridion.fields.converters;

import com.google.common.base.Strings;
import com.sdl.webapp.Legacy;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.sdl.webapp.common.util.StringUtils.toStrings;

@Component
@Legacy
public class DateFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = {FieldType.DATE};

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    public List<String> getStringValues(BaseField field) throws FieldConverterException {
        return toStrings(getFieldValues(field, DateTime.class));
    }

    @Override
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass, ModelBuilderPipeline builder) throws FieldConverterException {
        final List<DateTime> dateTimeValues = new ArrayList<>();

        for (String value : field.getDateTimeValues()) {
            if (!Strings.isNullOrEmpty(value)) {
                dateTimeValues.add(new DateTime(value));
            }
        }

        return dateTimeValues;
    }
}

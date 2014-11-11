package com.sdl.webapp.dd4t.fieldconverters;

import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XhtmlFieldConverter extends AbstractFieldConverter {
    private static final Logger LOG = LoggerFactory.getLogger(XhtmlFieldConverter.class);

    private static final FieldType[] SUPPORTED_FIELD_TYPES = { FieldType.Xhtml };

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass) throws FieldConverterException {
        // TODO: TSI-521 Content must be resolved; see [C#] DD4TModelBuilder.GetMultiLineStrings
        return field.getTextValues();
    }
}

package com.sdl.webapp.dd4t.fieldconverters;

import com.sdl.webapp.common.api.content.ContentResolver;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class XhtmlFieldConverter extends AbstractFieldConverter {
    private static final Logger LOG = LoggerFactory.getLogger(XhtmlFieldConverter.class);

    private static final FieldType[] SUPPORTED_FIELD_TYPES = { FieldType.XHTML };

    private final ContentResolver contentResolver;

    @Autowired
    public XhtmlFieldConverter(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass) throws FieldConverterException {
        final List<String> fieldValues = new ArrayList<>();
        for (String textValue : field.getTextValues()) {
            fieldValues.add(contentResolver.resolveContent(textValue));
        }
        return fieldValues;
    }
}

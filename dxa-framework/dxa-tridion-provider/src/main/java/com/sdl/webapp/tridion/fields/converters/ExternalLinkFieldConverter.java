package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
/**
 * <p>ExternalLinkFieldConverter class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class ExternalLinkFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = {FieldType.EXTERNALLINK};

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
        return field.getTextValues();
    }
}

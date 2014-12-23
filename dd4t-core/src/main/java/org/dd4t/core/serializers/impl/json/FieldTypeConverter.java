package org.dd4t.core.serializers.impl.json;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.dd4t.contentmodel.FieldType;

/**
 * @author Mihai Cadariu
 * @since 10.07.2014
 */
public class FieldTypeConverter extends StdConverter<Object, FieldType> {

    @Override
    public FieldType convert(Object value) {
        return value == null ? null : value instanceof FieldType ? (FieldType) value : FieldType.findByName((String) value);
    }
}

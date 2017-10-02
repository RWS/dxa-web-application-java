package org.dd4t.databind.serializers.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.dd4t.contentmodel.FieldType;

import java.io.IOException;

/**
 * FieldTypeSerializer.
 */
public class FieldTypeSerializer extends JsonSerializer<FieldType> {

    @Override
    public void serialize(FieldType value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeNumber(value.getValue());
    }
}
package com.sdl.dxa.api.datamodel.model.unknown;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;

public class UnknownModelDataSerializer extends JsonSerializer<UnknownModelData> {

    @Override
    public void serialize(UnknownModelData value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject(value);
        gen.writeRaw(value.getContent());
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(UnknownModelData value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        this.serialize(value, gen, serializers);
    }
}

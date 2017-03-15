package com.sdl.dxa.api.datamodel.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;

/**
 * {@link TypeResolverBuilder} for handling DXA-specific polymorphic logic. Handles type information for {@code java.lang.Object} in JSON.
 */
@Slf4j
public class ModelDataTypeResolver extends StdTypeResolverBuilder {

    @Override
    public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
        return useForType(baseType) ?
                (_includeAs == JsonTypeInfo.As.PROPERTY ?
                        _buildModelDataTypeSerializer(config, baseType, subtypes) :
                        super.buildTypeSerializer(config, baseType, subtypes)) :
                null;
    }

    @Override
    public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
        return useForType(baseType) ? super.buildTypeDeserializer(config, baseType, subtypes) : null;
    }

    private boolean useForType(JavaType t) {
        return t.isJavaLangObject();
    }

    private TypeSerializer _buildModelDataTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
        TypeIdResolver idRes = idResolver(config, baseType, subtypes, true, false);
        return new AsPropertyTypeSerializer(idRes, null, _typeProperty) {
            @Override
            public void writeTypePrefixForScalar(Object value, JsonGenerator g) throws IOException {
                // does nothing, we don't need type information for scalars
            }

            @Override
            public void writeTypePrefixForScalar(Object value, JsonGenerator g, Class<?> type) throws IOException {
                // does nothing, we don't need type information for scalars
            }

            @Override
            public void writeTypeSuffixForScalar(Object value, JsonGenerator g) throws IOException {
                // does nothing, we don't need type information for scalars
            }
        };
    }
}
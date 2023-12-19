package com.sdl.dxa.api.datamodel.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class ModelDataTypeResolverTest {

    @Mock
    private DeserializationConfig deserializationConfig;

    @Mock
    private SerializationConfig serializationConfig;

    private ModelDataTypeResolver resolver = new ModelDataTypeResolver();

    @BeforeEach
    public void init() {
        lenient().doReturn(null).when(deserializationConfig).findMixInClassFor(any());
        lenient().doReturn(DummyInterface.class).when(deserializationConfig).findMixInClassFor(eq(ModelDataTypeResolver.class));
        lenient().doReturn(PolymorphicObjectMixin.class).when(deserializationConfig).findMixInClassFor(eq(Object.class));
        lenient().doReturn(PolymorphicObjectMixin.class).when(deserializationConfig).findMixInClassFor(eq(SitemapItemModelData.class));

        lenient().doReturn(null).when(serializationConfig).findMixInClassFor(any());
        lenient().doReturn(DummyInterface.class).when(serializationConfig).findMixInClassFor(eq(ModelDataTypeResolver.class));
        lenient().doReturn(PolymorphicObjectMixin.class).when(serializationConfig).findMixInClassFor(eq(Object.class));
        lenient().doReturn(PolymorphicObjectMixin.class).when(serializationConfig).findMixInClassFor(eq(SitemapItemModelData.class));

        resolver.inclusion(JsonTypeInfo.As.PROPERTY).init(JsonTypeInfo.Id.CUSTOM, new ModelDataTypeIdResolver());
    }

    @Test
    public void shouldDetect_ThatPolymorphicMixin_IsSet() {
        //given

        //when
        TypeDeserializer deserializer1 = resolver.buildTypeDeserializer(deserializationConfig,
                SimpleType.constructUnsafe(Object.class), Collections.emptyList());
        TypeDeserializer deserializer2 = resolver.buildTypeDeserializer(deserializationConfig,
                SimpleType.constructUnsafe(SitemapItemModelData.class), Collections.emptyList());
        TypeDeserializer deserializer3 = resolver.buildTypeDeserializer(deserializationConfig,
                SimpleType.constructUnsafe(ModelDataTypeResolver.class), Collections.emptyList());
        TypeDeserializer deserializer4 = resolver.buildTypeDeserializer(deserializationConfig,
                SimpleType.constructUnsafe(SimpleType.class), Collections.emptyList());

        TypeSerializer serializer1 = resolver.buildTypeSerializer(serializationConfig,
                SimpleType.constructUnsafe(Object.class), Collections.emptyList());
        TypeSerializer serializer2 = resolver.buildTypeSerializer(serializationConfig,
                SimpleType.constructUnsafe(SitemapItemModelData.class), Collections.emptyList());
        TypeSerializer serializer3 = resolver.buildTypeSerializer(serializationConfig,
                SimpleType.constructUnsafe(ModelDataTypeResolver.class), Collections.emptyList());
        TypeSerializer serializer4 = resolver.buildTypeSerializer(serializationConfig,
                SimpleType.constructUnsafe(SimpleType.class), Collections.emptyList());


        //then
        assertNull(deserializer3);
        assertNull(serializer3);
        assertNull(deserializer4);
        assertNull(serializer4);

        assertNotNull(deserializer1);
        assertNotNull(deserializer2);
        assertNotNull(serializer1);
        assertNotNull(serializer2);
    }

    public interface DummyInterface {

    }
}
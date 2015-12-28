package org.dd4t.contentmodel.impl;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.serializers.Serializer;
import org.dd4t.core.serializers.impl.SerializerFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ComponentImplTest {

    private Serializer serializer;

    @Before
    public void setUp () throws Exception {
        serializer = new org.dd4t.core.serializers.impl.json.JSONSerializer();
        SerializerFactory.setSerializer(serializer);
    }

    @Test
    public void shouldHaveSameEclIdAfterDeSerializing () throws SerializationException {
        //given
        Component component = new ComponentImpl();
        ((ComponentImpl) component).setEclId("ecl:17-mm-379-dist-file");

        //when
        String content = serialize(component);
        ComponentImpl deserialized = SerializerFactory.deserialize(content, ComponentImpl.class);

        //then
        assertNotNull(deserialized);
        assertEquals(component.getEclId(), deserialized.getEclId());
    }

    @Test
    public void shouldHaveSameExtensionDataAfterDeSerializing () throws SerializationException {
        //given
        final String key = "ECL", key2 = "KEY2";
        Component component = new ComponentImpl();
        component.setExtensionData(new HashMap<String, FieldSet>() {{
            put(key, null);
            put(key2, null);
        }});

        //when
        String content = serialize(component);
        ComponentImpl deserialized = SerializerFactory.deserialize(content, ComponentImpl.class);

        //then
        assertNotNull(deserialized);
        assertTrue(component.getExtensionData().containsKey(key) && deserialized.getExtensionData().containsKey(key));
        assertTrue(component.getExtensionData().containsKey(key2) && deserialized.getExtensionData().containsKey(key2));
    }

    private String serialize (Object obj) throws SerializationException {
        return serializer.serialize(obj);
    }

}
package org.dd4t.core.serializers.impl.json;

import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Item;
import org.dd4t.contentmodel.impl.MultimediaImpl;
import org.dd4t.core.exceptions.SerializationException;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JSONSerializerTest {

    private Set<Class<? extends Field>> fields;
    private Set<Class<? extends Item>> items;

    JSONSerializer serializer;

    @Before
    public void setUp() throws SerializationException {

        Reflections reflections = new Reflections("org.dd4t.contentmodel");
        fields = reflections.getSubTypesOf(Field.class);
        items = reflections.getSubTypesOf(Item.class);

        // Initialize the serializer
        serializer = new JSONSerializer();
    }

    /**
     * This unit test, test if the items inherited from Item is serializable and deserializable
     */
    @Test
    public void testSerializationJItems() throws SerializationException, IllegalAccessException, InstantiationException {
        for (Class<? extends Item> item :items) {
            System.err.println("Test Item: " + item);

            if (Modifier.isAbstract(item.getModifiers())) {
                System.out.println(item + " is an abstract class, skipping in test");
                continue;
            }

            Item itemInstance = item.newInstance();
            String serialized = serializer.serialize(itemInstance);
            System.out.println(serialized);

            Item deserializedItem = serializer.deserialize(serialized, item);

            assertEquals("Item ID", itemInstance.getId(), deserializedItem.getId());
            assertEquals("Item Title", itemInstance.getId(), deserializedItem.getTitle());
        }
    }

    /**
     * This unit test, test if the fields inherited from Item is serializable and deserializable
     */
    @Test
    public void testSerializationFields() throws SerializationException, IllegalAccessException, InstantiationException {
        for (Class<? extends Field> field: fields) {

            if (Modifier.isAbstract(field.getModifiers())) {
                System.out.println(field + " is an abstract class, skipping in test");
                continue;
            }

            if (field.equals(MultimediaImpl.class)) {
                System.out.println("Skipping " + MultimediaImpl.class);
                continue;
            }

            Field fieldInstance = field.newInstance();
            fieldInstance.setName(field.toString());
            fieldInstance.setXPath("tcm:Content/test:label/test:label[1]/test:lKey");
            fieldInstance.setFieldType(FieldType.TEXT);

            String serialized = serializer.serialize(fieldInstance);

            Field deserializedItem = null;
            try {
                deserializedItem = serializer.deserialize(serialized, field);
            } catch (SerializationException e) {
                System.out.println(e.getMessage());
            }
            assertNotNull(deserializedItem);

            assertEquals("Name", fieldInstance.getName(), deserializedItem.getName());
            assertEquals("XPath", fieldInstance.getXPath(), deserializedItem.getXPath());

        }
    }
}
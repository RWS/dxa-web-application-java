package org.dd4t.core.serializers.impl;

import org.dd4t.contentmodel.exceptions.SerializationException;

/**
 * Serializer Interface
 *
 * @Author R. Kempees
 * @Since 05/06/14.
 */
public interface Serializer {
    /**
     * @param content serialized content string coming from the DD4T providers
     * @param aClass  concrete type of desired deserialized class
     * @param <T>
     * @return DD4T Tridion Model Object
     * @throws SerializationException
     */
    public <T> T deserialize(String content, Class<T> aClass) throws SerializationException;

    /**
     * @param item
     * @return serialized string
     * @throws SerializationException
     */
    public String serialize(Object item) throws SerializationException;
}

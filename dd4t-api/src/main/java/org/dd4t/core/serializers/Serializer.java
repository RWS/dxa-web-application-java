package org.dd4t.core.serializers;

import org.dd4t.core.exceptions.SerializationException;

/**
 * Serializer Interface. Use this to implement different
 * concrete Serializer or Databind classes and to hide the actual implementation.
 *
 * @author R. Kempees
 */
public interface Serializer {
	/**
	 * @param content serialized content string coming from the DD4T providers
	 * @param aClass  concrete type of desired deserialized class
	 * @param <T> The generic type of desired class
	 * @return DD4T Tridion Model Object
	 * @throws SerializationException
	 */
	public <T> T deserialize(String content, Class<T> aClass) throws SerializationException;

	/**
	 * @param item the Object to Serialize
	 * @return serialized string
	 * @throws SerializationException
	 */
	public String serialize(Object item) throws SerializationException;
}

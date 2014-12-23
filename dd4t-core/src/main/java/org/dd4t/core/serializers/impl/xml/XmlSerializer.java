package org.dd4t.core.serializers.impl.xml;

import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.serializers.Serializer;


/**
 * XML (De)Serializer
 *
 * @author R. Kempees
 */
public class XmlSerializer implements Serializer{

	/**
	 * TODO: implement XML version.
	 *
	 * @param content serialized content string coming from the DD4T providers
	 * @param aClass  concrete type of desired deserialized class
	 * @param <T> The desired type to deserialize to
	 * @return the deserialized object
	 * @throws SerializationException
	 */
	@Override public <T> T deserialize (final String content, final Class<T> aClass) throws SerializationException {
		return null;
	}

	/**
	 * TODO: implement.
	 *
	 * @param item A serializable item
	 * @return a serialized String representing the object
	 * @throws SerializationException
	 */
	@Override public String serialize (final Object item) throws SerializationException {
		return null;
	}
}

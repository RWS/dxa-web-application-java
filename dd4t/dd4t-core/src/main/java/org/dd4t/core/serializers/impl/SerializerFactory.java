package org.dd4t.core.serializers.impl;

import org.dd4t.contentmodel.exceptions.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Factory class to allow easy access for deserialization / serialization
 * of DD4T Tridion Objects.
 * <p/>
 * Needed to hide implementations (JSON, XML)
 * and to have only one instance through the entire application.
 *
 * @Author R. Kempees
 */
public class SerializerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SerializerFactory.class);

    private static Serializer serializer = null;

	@Autowired
	public SerializerFactory(Serializer serializerInstance)
	{
		serializer = serializerInstance;
	}
    /**
     * Deserialize a Tridion DD4T content String.
     * <p/>
     * Uses the concrete implementation configured in the context configuration.
     *
     * @param content
     * @param aClass
     * @param <T>     Concrete Type of DD4T model object
     * @return De-serialized DD4T Model Object.
     * @throws SerializationException
     */
    public static <T> T deserialize(String content, Class<T> aClass) throws SerializationException {
        LOG.trace("Using Serializer: {}", serializer.getClass());
        return serializer.deserialize(content, aClass);
    }
}

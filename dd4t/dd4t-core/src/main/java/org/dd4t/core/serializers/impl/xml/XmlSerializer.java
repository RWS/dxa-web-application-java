package org.dd4t.core.serializers.impl.xml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.core.serializers.impl.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * XMLDeserializer
 *
 * @author R. Kempees
 */
public class XmlSerializer implements Serializer{

	private static final Logger LOG = LoggerFactory.getLogger(XmlSerializer.class);
	private ObjectMapper mapper = new XmlMapper();

	@Override public <T> T deserialize (final String content, final Class<T> aClass) throws SerializationException {
		try {
			return mapper.readValue(content, aClass);
		} catch (IOException e) {
			LOG.error("Error deserializing.", e);
			throw new SerializationException(e);
		}
	}

	@Override public String serialize (final Object item) throws SerializationException {
		try {
			LOG.debug("Serializing a {}", item.getClass());
			return mapper.writeValueAsString(item);
		} catch (JsonProcessingException e) {
			LOG.error("Error serializing.", e);
			throw new SerializationException(e);
		}
	}
}

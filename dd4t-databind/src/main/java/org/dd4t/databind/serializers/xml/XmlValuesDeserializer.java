package org.dd4t.databind.serializers.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.List;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class XmlValuesDeserializer<T extends List> extends StdDeserializer<T> {

	public XmlValuesDeserializer (final Class<?> listClass) {
		super(listClass);
	}

	@Override public T deserialize (final JsonParser jsonParser, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
		final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
		final ObjectNode root = mapper.readTree(jsonParser);

		return null;
	}
}

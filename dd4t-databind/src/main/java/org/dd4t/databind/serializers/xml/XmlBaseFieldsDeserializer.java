package org.dd4t.databind.serializers.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.dd4t.contentmodel.Field;
import org.dd4t.databind.serializers.json.TridionFieldTypeIdResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class XmlBaseFieldsDeserializer<T extends Field> extends StdDeserializer<T> {

	private static final Logger LOG = LoggerFactory.getLogger(XmlBaseFieldsDeserializer.class);
	private static final XmlMapper XML_MAPPER = new XmlMapper();

	public XmlBaseFieldsDeserializer (final Class<T> fieldClass) {
		super(fieldClass);
	}

	@Override public T deserialize (final JsonParser jsonParser, final DeserializationContext ctxt) throws IOException, JsonProcessingException {

		final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
		final ObjectNode root = mapper.readTree(jsonParser);

		final Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
		while (fields.hasNext()) {
			final Map.Entry<String, JsonNode> element = fields.next();
			final String key = element.getKey();

			LOG.debug(element.getKey() + "  " + element.getValue().toString());

			if (key.equalsIgnoreCase("value")) {
				Class<?> clazz = null;
				final JsonNode valueNode = element.getValue();//.get("Field");
				if (valueNode.has("Field")) {
					final JsonNode field = valueNode.get("Field");
					clazz = getClassForFieldType(valueNode);
					LOG.debug("Deserializing field.");
					//Object fieldObject = XML_MAPPER.readValue(field.traverse(),clazz);
				//	LOG.debug(fieldObject.toString());
				}
			}
		}

			return null;
	}

	private static Class<?> getClassForFieldType (final JsonNode field) throws IOException {
		if (field != null) {
			if (field.has("FieldType")) {
				final String concreteType = TridionFieldTypeIdResolver.getClassForKey(field.get("FieldType").textValue());
				LOG.debug("Concrete Type: {}", concreteType);
				try {
					return ClassUtil.findClass(concreteType);
				} catch (ClassNotFoundException e) {
					throw new IOException(e);
				}
			}
		}
		return null;
	}
}

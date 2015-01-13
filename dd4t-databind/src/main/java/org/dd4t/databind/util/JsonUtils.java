package org.dd4t.databind.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.Field;
import org.dd4t.core.util.DateUtils;
import org.dd4t.core.util.TCMURI;
import org.dd4t.databind.builder.json.JsonDataBinder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class JsonUtils {
	private static final Logger LOG = LoggerFactory.getLogger(JsonUtils.class);
	private JsonUtils() {

	}
	public static <T extends Field> T renderComponentField(JsonNode node, Class<T> concreteClass) throws IOException {
		final JsonParser parser = node.traverse();
		return JsonDataBinder.getGenericMapper().readValue(parser,concreteClass);
	}

	public static TCMURI getTcmUriFromField (String fieldName, JsonNode node) {
		if (!node.has(fieldName)) {
			return null;
		}
		String tcmUri = node.get(fieldName).textValue();
		TCMURI uri = null;
		try {
			uri = new TCMURI(tcmUri);
		} catch (ParseException e) {
			LOG.error(e.getLocalizedMessage(),e);
		}
		return uri;
	}

	public static DateTime getDateFromField (String fieldName, JsonNode node) {
		if (!node.has(fieldName)) {
			return null;
		}
		String dateNode = node.get(fieldName).textValue();
		if (StringUtils.isEmpty(dateNode)){
			return null;
		}

		return DateUtils.convertStringToDate(dateNode);
	}
}

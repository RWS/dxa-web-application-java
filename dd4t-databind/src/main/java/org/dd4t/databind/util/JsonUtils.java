/*
 * Copyright (c) 2015 Radagio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    private JsonUtils () {

    }

    public static <T extends Field> T renderComponentField (JsonNode node, Class<T> concreteClass) throws IOException {
        final JsonParser parser = node.traverse();
        return JsonDataBinder.getGenericMapper().readValue(parser, concreteClass);
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
            LOG.error(e.getLocalizedMessage(), e);
        }
        return uri;
    }

    public static boolean isValidJsonNode (Object data) {

        return isNotNull(data) && isJsonNode(data);
    }

    public static boolean isJsonNode (Object data) {
        return data instanceof JsonNode;
    }

    public static boolean isNotNull (Object data) {
        return data != null;
    }

    public static DateTime getDateFromField (String fieldName, JsonNode node) {
        if (!node.has(fieldName)) {
            return null;
        }
        String dateNode = node.get(fieldName).textValue();
        if (StringUtils.isEmpty(dateNode)) {
            return null;
        }

        return DateUtils.convertStringToDate(dateNode);
    }
}

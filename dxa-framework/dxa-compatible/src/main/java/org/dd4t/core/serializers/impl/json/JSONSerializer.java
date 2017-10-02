/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
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

package org.dd4t.core.serializers.impl.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.dd4t.contentmodel.Field;
import org.dd4t.core.serializers.Serializer;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.databind.serializers.json.BaseFieldMixIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JSONSerializer
 *
 * @author R. Kempees
 */
public class JSONSerializer implements Serializer {

    private static final Logger LOG = LoggerFactory.getLogger(JSONSerializer.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.registerModule(new JodaModule());
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MAPPER.addMixIn(Field.class, BaseFieldMixIn.class);
        MAPPER.registerModule(new AfterburnerModule());
    }

    @Override
    public <T> T deserialize (final String content, final Class<T> aClass) throws SerializationException {
        try {
            return MAPPER.readValue(content, aClass);
        } catch (IOException e) {
            LOG.error("Error deserializing.", e);
            throw new SerializationException(e);
        }
    }

    @Override
    public String serialize (final Object item) throws SerializationException {
        try {
            LOG.debug("Serializing a {}", item.getClass());
            return MAPPER.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            LOG.error("Error serializing.", e);
            throw new SerializationException(e);
        }
    }
}

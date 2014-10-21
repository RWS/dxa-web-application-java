package org.dd4t.core.serializers.impl.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.core.serializers.impl.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JSONSerializer
 *
 * @Author R. Kempees
 * @Since 05.06.2014
 */
public class JSONSerializer implements Serializer {

    private static final Logger LOG = LoggerFactory.getLogger(JSONSerializer.class);
    /**
     * Jackson's 2.3.3 ObjectMapper
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    // Add JodaTime Serialization
    {
        mapper.registerModule(new JodaModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public <T> T deserialize(final String content, final Class<T> aClass) throws SerializationException {
        try {
            return mapper.readValue(content, aClass);
        } catch (IOException e) {
            LOG.error("Error deserializing.", e);
            throw new SerializationException(e);
        }
    }

    @Override
    public String serialize(final Object item) throws SerializationException {
        try {
            LOG.debug("Serializing a {}", item.getClass());
            return mapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            LOG.error("Error serializing.", e);
            throw new SerializationException(e);
        }
    }
}

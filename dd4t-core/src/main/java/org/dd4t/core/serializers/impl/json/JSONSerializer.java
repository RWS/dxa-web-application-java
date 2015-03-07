package org.dd4t.core.serializers.impl.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.dd4t.core.serializers.Serializer;
import org.dd4t.core.exceptions.SerializationException;
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
    /**
     * Jackson's ObjectMapper
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.registerModule(new JodaModule());
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public <T> T deserialize(final String content, final Class<T> aClass) throws SerializationException {
        try {
            return MAPPER.readValue(content, aClass);
        } catch (IOException e) {
            LOG.error("Error deserializing.", e);
            throw new SerializationException(e);
        }
    }

    @Override
    public String serialize(final Object item) throws SerializationException {
        try {
            LOG.debug("Serializing a {}", item.getClass());
            return MAPPER.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            LOG.error("Error serializing.", e);
            throw new SerializationException(e);
        }
    }
}

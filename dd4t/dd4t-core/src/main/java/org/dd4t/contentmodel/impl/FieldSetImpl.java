package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Embedded fields basically are an array of
 * ambiguous and unknown keynames, with multiple Fields in them
 */
public class FieldSetImpl implements FieldSet {

    private static final Logger LOG = LoggerFactory.getLogger(FieldSetImpl.class);
    // TODO: see if this doesn't eat up too much objects (and memory)
    private static final ObjectMapper mapper = new ObjectMapper();

    private Map<String, Object> rawContent = new HashMap<>();
    private Map<String, Field> content = new HashMap<>();

    @JsonProperty("Schema")
    private Schema schema;

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    @JsonAnyGetter
    public Map<String, Object> getRawContent() {
        return rawContent;
    }

    @JsonAnySetter
    public void set(String fieldKey, JsonNode embeddedField) {

        try {
            // The basefield annotations will map the fields to concrete types
            BaseField b = mapper.readValue(embeddedField.toString(), BaseField.class);
            content.put(fieldKey, b);
        } catch (IOException e) {
            LOG.error("Error deserializing FieldSet.", e);
        }

        rawContent.put(fieldKey, embeddedField);
    }

    /**
     * Get the content
     *
     * @return a map of field objects representing the content
     */
    public Map<String, Field> getContent() {
        return content;
    }

    /**
     * Set the content
     */
    public void setContent(Map<String, Field> content) {
        this.content = content;
    }
}

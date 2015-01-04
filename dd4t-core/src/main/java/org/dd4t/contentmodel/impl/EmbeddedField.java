package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Schema;

import java.util.LinkedList;
import java.util.List;

public class EmbeddedField extends BaseField implements Field {

    @JsonProperty("EmbeddedSchema")
    @JsonDeserialize(as = SchemaImpl.class)
    private Schema embeddedSchema;

    public EmbeddedField() {
        setFieldType(FieldType.EMBEDDED);
    }

    @Override
    public List<Object> getValues() {
        List<Object> list = new LinkedList<>();

        for (FieldSet fs : getEmbeddedValues()) {
            list.add(fs);
        }

        return list;
    }

    public Schema getEmbeddedSchema() {
        return embeddedSchema;
    }

    public void setEmbeddedSchema(final Schema embeddedSchema) {
        this.embeddedSchema = embeddedSchema;
    }
}
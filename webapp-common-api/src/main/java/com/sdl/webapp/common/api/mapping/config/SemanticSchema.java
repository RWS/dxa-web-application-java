package com.sdl.webapp.common.api.mapping.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.localization.Localization;

import java.util.List;
import java.util.Map;

public class SemanticSchema {

    @JsonProperty("Id")
    private long id;

    @JsonProperty("RootElement")
    private String rootElement;

    @JsonProperty("Fields")
    private List<SemanticSchemaField> fields;

    @JsonProperty("Semantics")
    private List<SchemaSemantics> semantics;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRootElement() {
        return rootElement;
    }

    public void setRootElement(String rootElement) {
        this.rootElement = rootElement;
    }

    public List<SemanticSchemaField> getFields() {
        return fields;
    }

    public void setFields(List<SemanticSchemaField> fields) {
        this.fields = ImmutableList.copyOf(fields);
    }

    public List<SchemaSemantics> getSemantics() {
        return semantics;
    }

    public void setSemantics(List<SchemaSemantics> semantics) {
        this.semantics = ImmutableList.copyOf(semantics);
    }

    /**
     * Returns a map in which the keys are vocabulary identifiers and the keys are entity names; the map contains
     * information for all entities defined in this schema.
     *
     * @param localization The localization.
     * @return A {@code Map} containing entity names by vocabulary identifiers.
     */
    public Map<String, String> getEntityNamesByVocabulary(Localization localization) {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (SchemaSemantics schemaSemantics : semantics) {
            final SemanticVocabulary v = localization.getSemanticVocabularies().get(schemaSemantics.getPrefix());
            builder.put(v.getVocabulary(), schemaSemantics.getEntity());
        }

        return builder.build();
    }
}

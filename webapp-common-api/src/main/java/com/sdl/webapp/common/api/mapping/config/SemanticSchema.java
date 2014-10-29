package com.sdl.webapp.common.api.mapping.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.sdl.webapp.common.api.localization.Localization;

import java.util.List;

/**
 * Semantic schema.
 *
 * This contains semantic mapping configuration information that comes from the content provider. This information is
 * loaded as part of the configuration of a {@code Localization} by the {@code LocalizationFactory}.
 *
 * Semantic schemas are normally loaded from the configuration file: {@code /system/mappings/schemas.json}
 */
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

    public boolean hasSchemaSemantics(String vocabulary, String entityName, Localization localization) {
//        final Map<String, SemanticVocabulary> semanticVocabularies = localization.getSemanticVocabularies();
//        for (SchemaSemantics schemaSemantics : semantics) {
//            if (vocabulary.equals(semanticVocabularies.get(schemaSemantics.getPrefix()).getVocabulary()) &&
//                    entityName.equals(schemaSemantics.getEntityName())) {
//                return true;
//            }
//        }

        return false;
    }

    public SemanticSchemaField findFieldBySemantics(String vocabulary, String entityName, String propertyName, Localization localization) {
//        final Map<String, SemanticVocabulary> semanticVocabularies = localization.getSemanticVocabularies();
//        for (SemanticSchemaField field : fields) {
//            for (FieldSemantics fieldSemantics : field.getSemantics()) {
//                if (vocabulary.equals(semanticVocabularies.get(fieldSemantics.getPrefix()).getVocabulary()) &&
//                        entityName.equals(fieldSemantics.getEntityName()) &&
//                        propertyName.equals(fieldSemantics.getPropertyName())) {
//                    return field;
//                }
//            }
//        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SemanticSchema that = (SemanticSchema) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "SemanticSchema{" +
                "id=" + id +
                ", rootElement='" + rootElement + '\'' +
                ", fields=" + fields +
                ", semantics=" + semantics +
                '}';
    }
}

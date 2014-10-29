package com.sdl.webapp.common.impl.localization.semantics;

import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.api.mapping.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class SemanticsConverter {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticsConverter.class);

    private SemanticsConverter() {
    }

    public static List<SemanticSchema> convertSemantics(List<JsonSchema> jsonSchemas,
                                                        List<JsonVocabulary> jsonVocabularies)
            throws LocalizationFactoryException {
        final Map<String, SemanticVocabulary> vocabularies = new HashMap<>();
        for (JsonVocabulary jsonVocabulary : jsonVocabularies) {
            final String prefix = jsonVocabulary.getPrefix();
            if (vocabularies.put(prefix, new SemanticVocabulary(jsonVocabulary.getVocab())) != null) {
                throw new LocalizationFactoryException("Error in semantic vocabulary configuration: There are " +
                        "multiple vocabularies with the same prefix '" + prefix + "'.");
            }
        }
        LOG.trace("vocabularies={}", vocabularies);

        final List<SemanticSchema> schemas = new ArrayList<>();
        for (JsonSchema jsonSchema : jsonSchemas) {
            schemas.add(new SemanticSchema(jsonSchema.getId(), jsonSchema.getRootElement(),
                    createEntitySemantics(jsonSchema.getSemantics(), vocabularies),
                    createSemanticFields(jsonSchema.getFields(), vocabularies)));
        }
        LOG.trace("schemas={}", schemas);

        return schemas;
    }

    private static Set<EntitySemantics> createEntitySemantics(List<JsonSchemaSemantics> jsonSchemaSemanticses,
                                                              Map<String, SemanticVocabulary> vocabularies)
            throws LocalizationFactoryException {
        final Set<EntitySemantics> result = new HashSet<>();

        for (JsonSchemaSemantics jsonSchemaSemantics : jsonSchemaSemanticses) {
            final String prefix = jsonSchemaSemantics.getPrefix();
            final SemanticVocabulary vocabulary = vocabularies.get(prefix);
            if (vocabulary == null) {
                throw new LocalizationFactoryException("Error in semantic schema configuration: Found schema " +
                        "semantics which uses the prefix '" + prefix + "', but there is no vocabulary with this " +
                        "prefix.");
            }

            final EntitySemantics entitySemantics = new EntitySemantics(vocabularies.get(prefix),
                    jsonSchemaSemantics.getEntity());

            result.add(entitySemantics);
        }

        return result;
    }

    private static Map<FieldSemantics, SemanticField> createSemanticFields(List<JsonSchemaField> jsonSchemaFields,
                                                                           Map<String, SemanticVocabulary> vocabularies)
            throws LocalizationFactoryException {
        final Map<FieldSemantics, SemanticField> result = new HashMap<>();

        for (JsonSchemaField jsonSchemaField : jsonSchemaFields) {
            final SemanticField semanticField = new SemanticField(jsonSchemaField.getName(), jsonSchemaField.getPath(),
                    jsonSchemaField.isMultiValue(), createSemanticFields(jsonSchemaField.getFields(), vocabularies));

            for (JsonFieldSemantics jsonFieldSemantics : jsonSchemaField.getSemantics()) {
                final String prefix = jsonFieldSemantics.getPrefix();
                final SemanticVocabulary vocabulary = vocabularies.get(prefix);
                if (vocabulary == null) {
                    throw new LocalizationFactoryException("Error in semantic schema configuration: Found field " +
                            "semantics which uses the prefix '" + prefix + "', but there is no vocabulary with this " +
                            "prefix.");
                }

                final FieldSemantics fieldSemantics = new FieldSemantics(vocabulary, jsonFieldSemantics.getEntity(),
                        jsonFieldSemantics.getProperty());

                result.put(fieldSemantics, semanticField);
            }
        }

        return result;
    }
}

package com.sdl.webapp.common.impl.localization.semantics;

import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.api.mapping.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Semantics converter.
 *
 * The web application needs the semantics configuration information in a different format than what is provided in
 * the JSON configuration files for the localization. This class converts the semantics into the format that the web
 * application needs.
 */
public final class SemanticsConverter {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticsConverter.class);

    private SemanticsConverter() {
    }

    /**
     * Converts semantics configuration information from the configuration files into the format that the web
     * application needs.
     *
     * @param jsonSchemas A list of {@code JsonSchema} objects from the localization configuration.
     * @param jsonVocabularies A list of {@code JsonVocabulary} objects from the localization configuration.
     * @return A list of {@code SemanticSchema} objects.
     * @throws LocalizationFactoryException If there is an error in the configuration data.
     */
    public static List<SemanticSchema> convertSemantics(List<JsonSchema> jsonSchemas,
                                                        List<JsonVocabulary> jsonVocabularies)
            throws LocalizationFactoryException {
        // Build a map of vocabularies by prefix
        final Map<String, SemanticVocabulary> vocabularies = new HashMap<>();
        for (JsonVocabulary jsonVocabulary : jsonVocabularies) {
            final String prefix = jsonVocabulary.getPrefix();
            if (vocabularies.put(prefix, new SemanticVocabulary(jsonVocabulary.getVocab())) != null) {
                throw new LocalizationFactoryException("Error in semantic vocabulary configuration: There are " +
                        "multiple vocabularies with the same prefix '" + prefix + "'.");
            }
        }
        LOG.trace("vocabularies={}", vocabularies);

        // Build semantic schemas
        final List<SemanticSchema> schemas = new ArrayList<>();
        for (JsonSchema jsonSchema : jsonSchemas) {
            schemas.add(new SemanticSchema(jsonSchema.getId(), jsonSchema.getRootElement(),
                    createEntitySemantics(jsonSchema.getSemantics(), vocabularies),
                    createSemanticFields(jsonSchema.getFields(), vocabularies)));
        }
        LOG.trace("schemas={}", schemas);

        return schemas;
    }

    /**
     * Creates a set of {@code EntitySemantics} for the schema semantics in the configuration data.
     *
     * @param jsonSchemaSemanticsList Schema semantics from the configuration data.
     * @param vocabularies The map of vocabularies by prefix.
     * @return A set of {@code EntitySemantics}.
     * @throws LocalizationFactoryException If there are schema semantics which use a prefix for which there is no
     *      vocabulary.
     */
    private static Set<EntitySemantics> createEntitySemantics(List<JsonSchemaSemantics> jsonSchemaSemanticsList,
                                                              Map<String, SemanticVocabulary> vocabularies)
            throws LocalizationFactoryException {
        final Set<EntitySemantics> result = new HashSet<>();

        for (JsonSchemaSemantics jsonSchemaSemantics : jsonSchemaSemanticsList) {
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

    /**
     * Creates a map of semantic fields by field semantics for the schema fields in the configuration data.
     *
     * @param jsonSchemaFields Schema fields from the configuration data.
     * @param vocabularies The map of vocabularies by prefix.
     * @return A map of {@code SemanticField} objects by {@code FieldSemantics}.
     * @throws LocalizationFactoryException If there are field semantics which use a prefix for which there is no
     *      vocabulary.
     */
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

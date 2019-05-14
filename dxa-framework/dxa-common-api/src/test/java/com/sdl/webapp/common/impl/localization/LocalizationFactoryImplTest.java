package com.sdl.webapp.common.impl.localization;

import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE_VOCABULARY;
import static org.junit.Assert.assertEquals;

public class LocalizationFactoryImplTest {

    private LocalizationFactoryImpl localizationFactory;

    @Before
    public void init() {
        this.localizationFactory = new LocalizationFactoryImpl();
    }


    @Test
    public void shouldAddDocsTopicSchema() throws LocalizationFactoryException {

        SemanticSchema topicSchema = localizationFactory.getTopicSchema();
        assertEquals(1, topicSchema.getId());

        Map<FieldSemantics, SemanticField> semanticFields = topicSchema.getSemanticFields();
        assertEquals(2, semanticFields.size());

        ensureOnlyOneField(semanticFields, "topicTitle", "title");
        ensureOnlyOneField(semanticFields, "topicBody", "topic");
    }

    private List<Map.Entry<FieldSemantics, SemanticField>> getSemanticsForElement(Map<FieldSemantics, SemanticField> fields, String elementName) {
        return fields.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getPropertyName().equals(elementName))
                .collect(Collectors.toList());
    }

    private void ensureOnlyOneField(Map<FieldSemantics, SemanticField> fields, String elementName, String fieldName) {
        List<Map.Entry<FieldSemantics, SemanticField>> semanticFields = getSemanticsForElement(fields, elementName);
        assertEquals(1, semanticFields.size());

        Map.Entry<FieldSemantics, SemanticField> fieldSemantics = semanticFields.get(0);
        assertEquals(elementName, fieldSemantics.getKey().getPropertyName());
        assertEquals(SDL_CORE_VOCABULARY, fieldSemantics.getKey().getVocabulary());
        assertEquals(fieldName, fieldSemantics.getValue().getName());

    }
}
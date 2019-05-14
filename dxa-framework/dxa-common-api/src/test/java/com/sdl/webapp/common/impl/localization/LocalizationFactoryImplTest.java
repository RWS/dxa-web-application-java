package com.sdl.webapp.common.impl.localization;

import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE_VOCABULARY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocalizationFactoryImplTest {


    @Test
    public void shouldAddDocsTopicSchema() throws LocalizationFactoryException {
        LocalizationFactoryImpl factory = new LocalizationFactoryImpl();

        SemanticSchema topicSchema = factory.getTopicSchema();
        assertEquals(1, topicSchema.getId());

        Map<FieldSemantics, SemanticField> semanticFields = topicSchema.getSemanticFields();
        assertEquals(2, semanticFields.size());

        String topicTitleElem = "topicTitle";
        String topicBodyElem = "topicBody";

        Optional<Map.Entry<FieldSemantics, SemanticField>> titleSemantics = semanticFields.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getPropertyName().equals(topicTitleElem))
                .findFirst();

        assertTrue(titleSemantics.isPresent());
        assertEquals(topicTitleElem, titleSemantics.get().getKey().getPropertyName());
        assertEquals(SDL_CORE_VOCABULARY, titleSemantics.get().getKey().getVocabulary());
        assertEquals("title", titleSemantics.get().getValue().getName());

        Optional<Map.Entry<FieldSemantics, SemanticField>> bodySemantics = semanticFields.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getPropertyName().equals(topicBodyElem))
                .findFirst();

        assertTrue(bodySemantics.isPresent());
        assertEquals(topicBodyElem, bodySemantics.get().getKey().getPropertyName());
        assertEquals(SDL_CORE_VOCABULARY, bodySemantics.get().getKey().getVocabulary());
        assertEquals("topic", bodySemantics.get().getValue().getName());
    }

}
package com.sdl.webapp.common.impl.markup;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntityInfo;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticPropertyInfo;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.markup.Markup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@code Markup}.
 */
@ContextConfiguration(classes = MarkupTest.MarkupTestConfig.class)
public class MarkupTest {

    @Configuration
    @Profile("test")
    public static class MarkupTestConfig {

        @Bean
        public Markup markup() throws NoSuchFieldException {
            return new MarkupImpl(semanticMappingRegistry(), webRequestContext());
        }

        @Bean
        public SemanticMappingRegistry semanticMappingRegistry() throws NoSuchFieldException {
            final SemanticMappingRegistry semanticMappingRegistry = mock(SemanticMappingRegistry.class);

            final Set<SemanticEntityInfo> entityInfoList = new LinkedHashSet<>();
            entityInfoList.add(new SemanticEntityInfo(mockSemanticEntity("TestEnt", "", "", "", true), TestEntity.class));
            entityInfoList.add(new SemanticEntityInfo(mockSemanticEntity("SchemaEnt", "", SemanticVocabulary.SCHEMA_ORG, "s", true), TestEntity.class));
            when(semanticMappingRegistry.getEntityInfo(TestEntity.class)).thenReturn(entityInfoList);

            final Field testField = TestEntity.class.getDeclaredField("testField");
            final Field hiddenField = TestEntity.class.getDeclaredField("hiddenField");

            final Set<SemanticPropertyInfo> propertyInfoList = new LinkedHashSet<>();
            propertyInfoList.add(new SemanticPropertyInfo(mockSemanticProperty("s:TheField", ""), testField));
            propertyInfoList.add(new SemanticPropertyInfo(mockSemanticProperty("SomeField", ""), hiddenField));

            when(semanticMappingRegistry.getPropertyInfo(testField)).thenReturn(propertyInfoList);

            return semanticMappingRegistry;
        }

        @Bean
        public WebRequestContext webRequestContext() {
            final WebRequestContext webRequestContext = mock(WebRequestContext.class);

            when(webRequestContext.getContextPath()).thenReturn("/test");

            final Localization localization = mock(Localization.class);
            when(localization.getVersion()).thenReturn("v0.5");
            when(localization.localizePath("/system/v0.5/example")).thenReturn("/xyz/system/v0.5/example");

            when(localization.getLocale()).thenReturn(Locale.US);

            when(localization.getResource("core.todayText")).thenReturn("TODAY");
            when(localization.getResource("core.yesterdayText")).thenReturn("YESTERDAY");
            when(localization.getResource("core.xDaysAgoText")).thenReturn("{0} DAYS AGO");

            when(webRequestContext.getLocalization()).thenReturn(localization);

            when(webRequestContext.isPreview()).thenReturn(true);

            return webRequestContext;
        }

        private SemanticEntity mockSemanticEntity(String entityName, String value, String vocabulary, String prefix, boolean public_) {
            final SemanticEntity anno = mock(SemanticEntity.class);
            when(anno.entityName()).thenReturn(entityName);
            when(anno.value()).thenReturn(value);
            when(anno.vocabulary()).thenReturn(vocabulary);
            when(anno.prefix()).thenReturn(prefix);
            when(anno.public_()).thenReturn(public_);
            return anno;
        }

        private SemanticProperty mockSemanticProperty(String propertyName, String value) {
            final SemanticProperty anno = mock(SemanticProperty.class);
            when(anno.propertyName()).thenReturn(propertyName);
            when(anno.value()).thenReturn(value);
            return anno;
        }

        public static class TestEntity extends AbstractEntityModel {
            private String testField;
            private String hiddenField;
        }
    }
}

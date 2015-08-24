package com.sdl.webapp.common.impl.markup;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntityInfo;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.annotations.SemanticPropertyInfo;
import com.sdl.webapp.common.api.mapping.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.markup.Markup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class MarkupTestConfig {

    public static class TestEntity extends AbstractEntity {
        private String testField;
        private String hiddenField;
    }

    @Bean
    public Markup markup() throws NoSuchFieldException {
        return new MarkupImpl(semanticMappingRegistry(), webRequestContext());
    }

    @Bean
    public SemanticMappingRegistry semanticMappingRegistry() throws NoSuchFieldException {
        final SemanticMappingRegistry semanticMappingRegistry = mock(SemanticMappingRegistry.class);

        final List<SemanticEntityInfo> entityInfoList = new ArrayList<>();
        entityInfoList.add(new SemanticEntityInfo(mockSemanticEntity("TestEnt", "", "", "", true), TestEntity.class));
        entityInfoList.add(new SemanticEntityInfo(mockSemanticEntity("SchemaEnt", "", SemanticVocabulary.SCHEMA_ORG, "s", true), TestEntity.class));
        when(semanticMappingRegistry.getEntityInfo(TestEntity.class)).thenReturn(entityInfoList);

        final Field testField = TestEntity.class.getDeclaredField("testField");
        final Field hiddenField = TestEntity.class.getDeclaredField("hiddenField");

        final List<SemanticPropertyInfo> propertyInfoList = new ArrayList<>();
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
}

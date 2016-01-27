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
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.Markup;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@code Markup}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class MarkupTest {

    @Autowired
    private Markup markup;

    @Test
    public void testUrl() {
        assertThat(markup.url("/example"), is("/test/example"));
    }

    @Test
    public void testVersionedContent() {
        assertThat(markup.versionedContent("/example"), is("/test/xyz/system/v0.5/example"));
    }

    @Test
    public void testRegion() {
        final RegionModelImpl region;
        try {
            region = new RegionModelImpl("TestRegion");
            assertThat(markup.region(region), is("typeof=\"Region\" resource=\"TestRegion\""));
        } catch (DxaException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEntity() {
        assertThat(markup.entity(new MarkupTestConfig.TestEntity()),
                is("prefix=\"s: http://schema.org/\" typeof=\"s:SchemaEnt\""));
    }

    @Test
    public void testPublicProperty() {
        assertThat(markup.property(new MarkupTestConfig.TestEntity(), "testField"), is("property=\"s:TheField\""));
    }

    @Test
    public void testPrivateProperty() {
        // Field which has an empty prefix; the vocabulary with the empty prefix is implicitly private,
        // so there should be no 'property' attribute for this field
        assertThat(markup.property(new MarkupTestConfig.TestEntity(), "hiddenField"), is(""));
    }

    @Test
    public void testResource() {
        assertThat(markup.resource("core.todayText"), is("TODAY"));
    }

    @Test
    public void testFormatDate() {
        assertThat(markup.formatDate(new DateTime(2014, 12, 11, 15, 39, 8, 834)), is("Thursday, December 11, 2014"));
    }

    @Test
    public void testFormatDateDiffToday() {
        assertThat(markup.formatDateDiff(DateTime.now()), is("TODAY"));
    }

    @Test
    public void testFormatDateDiffYesterday() {
        assertThat(markup.formatDateDiff(DateTime.now().minusDays(1)), is("YESTERDAY"));
    }

    @Test
    public void testFormatDateDiffXDaysAgo() {
        for (int i = 2; i <= 7; i++) {
            assertThat(markup.formatDateDiff(DateTime.now().minusDays(i)), is(i + " DAYS AGO"));
        }
    }

    @Test
    public void testFormatDateDiffLongerAgo() {
        for (int i = 8; i < 400; i++) {
            final DateTime dateTime = DateTime.now().minusDays(i);
            final String expected = DateTimeFormat.forPattern("d MMM yyyy").withLocale(Locale.US).print(dateTime);
            assertThat(markup.formatDateDiff(dateTime), is(expected));
        }
    }

    @Test
    public void testFormatMessage() {
        assertThat(markup.formatMessage("Hello {0}, the weather is {1} today", "World", "cold"),
                is("Hello World, the weather is cold today"));
    }

    @Test
    public void testReplaceLineEndsWithHtmlBreaks() {
        assertThat(markup.replaceLineEndsWithHtmlBreaks("Hello. Sunny World."), is("Hello<br/>Sunny World."));
    }

    @Configuration
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

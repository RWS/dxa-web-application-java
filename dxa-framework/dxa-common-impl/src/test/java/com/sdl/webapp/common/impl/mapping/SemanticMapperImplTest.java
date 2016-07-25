package com.sdl.webapp.common.impl.mapping;

import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.mapping.semantic.FieldData;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG_VOCABULARY;
import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE_VOCABULARY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class SemanticMapperImplTest {

    @Autowired
    private SemanticMapperImpl semanticMapper;

    @Test
    public void shouldCorrectlyResolveSemanticMappings() throws SemanticMappingException, NoSuchFieldException {
        //given
        final SemanticField headlineField = semanticField("headline", "/TestArticle/headline");

        final SemanticField subheading = semanticField("subheading", "/TestArticle/articleBody/subheading");
        final Map<FieldSemantics, SemanticField> articleBodyFields = new SemanticMapBuilder()
                .putSdlCore("TestParagraph", "subheading", subheading)
                .build();

        final SemanticField dateCreatedField = semanticField("dateCreated", "/Metadata/standardMeta/dateCreated");
        final Map<FieldSemantics, SemanticField> standardMetaFields = new SemanticMapBuilder()
                .putSdlCore("StandardMetadata", "dateCreated", dateCreatedField)
                .putSchemaOrg("TestArticle", "dateCreated", dateCreatedField)
                .build();

        final SemanticField articleBody = semanticField(articleBodyFields, "articleBody", "/TestArticle/articleBody", true);
        final Map<FieldSemantics, SemanticField> schemaFields = new SemanticMapBuilder()
                .putBoth("TestArticle", "headline", headlineField)
                .putSdlCore("TestArticle", "articleBody", articleBody)
                .putSchemaOrg("TestArticle", "articleBody", semanticField(Collections.<FieldSemantics, SemanticField>emptyMap(), "articleBody", "/TestArticle/articleBody", true))
                .putSdlCore("TestArticle", "standardMeta", semanticField(standardMetaFields, "standardMeta", "/Metadata/standardMeta", false))
                .build();

        // headline in Article
        final SemanticFieldDataProvider fieldDataProvider = mock(SemanticFieldDataProvider.class);
        when(fieldDataProvider.getFieldData(headlineField, new TypeDescriptor(TestArticle.class.getDeclaredField("headline"))))
                .thenReturn(new FieldData("HEADLINE", "HeadlineField"));

        // dattTime in Article
        final DateTime dateTime = new DateTime(2014, 11, 4, 13, 14, 34, 123, DateTimeZone.UTC);
        when(fieldDataProvider.getFieldData(dateCreatedField, new TypeDescriptor(TestArticle.class.getDeclaredField("date"))))
                .thenReturn(new FieldData(dateTime, "DateCreatedField"));

        //subheading in Paragraphs
        String paragraphSubheading1 = "SUBHEADING";
        String paragraphSubheading2 = "SUBHEADING_2";
        when(fieldDataProvider.getFieldData(articleBody, new TypeDescriptor(TestArticle.class.getDeclaredField("articleBody"))))
                .thenReturn(new FieldData(newArrayList(new TestParagraph(paragraphSubheading1), new TestParagraph(paragraphSubheading2)), "ArticleBody"));
        when(fieldDataProvider.getFieldData(subheading, new TypeDescriptor(TestParagraph.class.getDeclaredField("subheading"))))
                .thenReturn(new FieldData(paragraphSubheading1, "subheading"), new FieldData(paragraphSubheading2, "subheading"));

        //when
        final TestArticle article = semanticMapper.createEntity(TestArticle.class, schemaFields, fieldDataProvider);

        //then
        assertThat(article.getHeadline(), is("HEADLINE"));
        assertThat(article.getDate(), is(dateTime));
        Iterator<TestParagraph> iterator = article.getArticleBody().iterator();
        assertEquals(paragraphSubheading1, iterator.next().getSubheading());
        assertEquals(paragraphSubheading2, iterator.next().getSubheading());

        final Map<String, String> propertyData = article.getXpmPropertyMetadata();
        assertThat(propertyData.entrySet(), hasSize(3));
        assertThat(propertyData, hasEntry("articleBody", "ArticleBody"));
        assertThat(propertyData, hasEntry("headline", "HeadlineField"));
        assertThat(propertyData, hasEntry("date", "DateCreatedField"));
    }

    @NotNull
    private SemanticField semanticField(String subheading, String path) {
        return new SemanticField(subheading, path, false, Collections.<FieldSemantics, SemanticField>emptyMap());
    }

    @NotNull
    private SemanticField semanticField(Map<FieldSemantics, SemanticField> values, String subheading, String path, boolean multiValue) {
        return new SemanticField(subheading, path, multiValue, values);
    }

    private static class SemanticMapBuilder extends ImmutableMap.Builder<FieldSemantics, SemanticField> {

        SemanticMapBuilder putBoth(String entityName, String propertyName, SemanticField field) {
            return putSdlCore(entityName, propertyName, field).putSchemaOrg(entityName, propertyName, field);
        }

        SemanticMapBuilder putSdlCore(String entityName, String propertyName, SemanticField field) {
            return (SemanticMapBuilder) put(new FieldSemantics(SDL_CORE_VOCABULARY, entityName, propertyName), field);
        }

        SemanticMapBuilder putSchemaOrg(String entityName, String propertyName, SemanticField field) {
            return (SemanticMapBuilder) put(new FieldSemantics(SCHEMA_ORG_VOCABULARY, entityName, propertyName), field);
        }
    }

    @Configuration
    public static class SemanticMapperImplTestConfig {

        @Bean
        public SemanticMapperImpl semanticMapperImpl() {
            return new SemanticMapperImpl(semanticMappingRegistry());
        }

        SemanticMappingRegistry semanticMappingRegistry() {
            SemanticMappingRegistryImpl semanticMappingRegistry = new SemanticMappingRegistryImpl();
            semanticMappingRegistry.registerEntity(TestArticle.class);
            semanticMappingRegistry.registerEntity(TestParagraph.class);
            return semanticMappingRegistry;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    private static class TestParagraph extends AbstractEntityModel {

        private String subheading;
    }

    @EqualsAndHashCode(callSuper = true)
    @SemanticEntity(entityName = "TestArticle", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
    @Data
    private static class TestArticle extends AbstractEntityModel {

        @SemanticProperty("s:headline")
        private String headline;

        @SemanticProperty("s:articleBody")
        private List<TestParagraph> articleBody;

        @SemanticProperty("s:dateCreated")
        private DateTime date;
    }
}

package com.sdl.webapp.common.impl.mapping;

import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.mapping.semantic.FieldData;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
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

import java.lang.reflect.Field;
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
@ContextConfiguration(classes = SemanticMapperImplTest.SemanticMapperImplTestConfig.class)
public class SemanticMapperImplTest {

    @Autowired
    private SemanticMapperImpl semanticMapper;

    private SemanticFieldDataProvider fieldDataProvider = mock(SemanticFieldDataProvider.class);

    @NotNull
    private static SemanticField semanticField(String fieldName, String path) {
        return semanticField(fieldName, path, Collections.emptyMap(), false);
    }

    @NotNull
    private static SemanticField semanticField(String fieldName, String path, Map<FieldSemantics, SemanticField> values, boolean multiValue) {
        return new SemanticField(fieldName, path, multiValue, values);
    }

    private void mockData(Field field, SemanticField semanticField, FieldData fieldData, FieldData... rest) throws SemanticMappingException {
        when(fieldDataProvider.getFieldData(semanticField, new TypeDescriptor(field))).thenReturn(fieldData, rest);
    }


    @Test
    public void shouldCorrectlyResolveSemanticMappings() throws SemanticMappingException, NoSuchFieldException {
        //given
        SemanticSchema semanticSchema = new SemanticSchema(1L, "not important", Collections.emptySet(), TestArticle.getSemantics());
        when(fieldDataProvider.getSemanticSchema()).thenReturn(semanticSchema);

        // headline in Article
        mockData(TestArticle.class.getDeclaredField("headline"), TestArticle.SEMANTIC_FIELDS.get("headline"), new FieldData("HEADLINE", "tcm:Content/HeadlineField"));

        // dattTime in Article
        DateTime dateTime = new DateTime(2014, 11, 4, 13, 14, 34, 123, DateTimeZone.UTC);
        mockData(TestArticle.class.getDeclaredField("date"), TestArticle.SEMANTIC_FIELDS.get("date"), new FieldData(dateTime, "tcm:Content/DateCreatedField"));

        //subheading in Paragraphs
        String subheading1 = "SUBHEADING";
        String subheading2 = "SUBHEADING_2";

        List<TestParagraph> articleBody = newArrayList(new TestParagraph(subheading1), new TestParagraph(subheading2));

        mockData(TestArticle.class.getDeclaredField("articleBody"), TestArticle.SEMANTIC_FIELDS.get("articleBody"), new FieldData(articleBody, "tcm:Content/ArticleBody"));

        mockData(TestParagraph.class.getDeclaredField("subheading"), TestParagraph.SEMANTIC_FIELDS.get("subheading"), new FieldData(subheading1, "subheading"), new FieldData(subheading2, "tcm:Content/subheading"));

        mockData(TestArticle.class.getDeclaredField("manyMappings"), TestArticle.SEMANTIC_FIELDS.get("mMapping2"), new FieldData("mMapping2Value", "tcm:Content/mMapping2"));


        //when
        TestArticle article = semanticMapper.createEntity(TestArticle.class, TestArticle.getSemantics(), fieldDataProvider);


        //then
        assertThat(article.getHeadline(), is("HEADLINE"));
        assertThat(article.getDate(), is(dateTime));
        assertThat(article.getManyMappings(), is("mMapping2Value"));
        Iterator<TestParagraph> iterator = article.getArticleBody().iterator();
        assertEquals(subheading1, iterator.next().getSubheading());
        assertEquals(subheading2, iterator.next().getSubheading());

        Map<String, String> xpmMetadata = article.getXpmPropertyMetadata();
        assertThat(xpmMetadata.entrySet(), hasSize(6));
        assertThat(xpmMetadata, hasEntry("articleBody", "tcm:Content/ArticleBody"));
        assertThat(xpmMetadata, hasEntry("headline", "tcm:Content/HeadlineField"));
        assertThat(xpmMetadata, hasEntry("date", "tcm:Content/DateCreatedField"));
        assertThat(xpmMetadata, hasEntry("emptyField", "tcm:Content/custom:TestArticle/custom:emptyField"));
        assertThat(xpmMetadata, hasEntry("manyMappings", "tcm:Content/mMapping2"));
        assertThat(xpmMetadata, hasEntry("manyMappingsNoValue", "tcm:Content/custom:TestArticle/custom:manyMappingsNoValue"));
    }

    private static class SemanticMapBuilder extends ImmutableMap.Builder<FieldSemantics, SemanticField> {

        SemanticMapBuilder both(String entityName, String propertyName, SemanticField field) {
            return sdlCore(entityName, propertyName, field)
                    .schemaOrg(entityName, propertyName, field);
        }

        SemanticMapBuilder sdlCore(String entityName, String propertyName, SemanticField field) {
            return (SemanticMapBuilder) put(new FieldSemantics(SDL_CORE_VOCABULARY, entityName, propertyName), field);
        }

        SemanticMapBuilder schemaOrg(String entityName, String propertyName, SemanticField field) {
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

        final static Map<String, SemanticField> SEMANTIC_FIELDS = new ImmutableMap.Builder<String, SemanticField>()
                .put("subheading", semanticField("subheading", "/TestArticle/articleBody/subheading"))
                .build();

        private String subheading;
    }

    @EqualsAndHashCode(callSuper = true)
    @SemanticEntities({
            @SemanticEntity(entityName = "TestArticle", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true),
            @SemanticEntity(entityName = "MyTestArticle", vocabulary = SCHEMA_ORG, prefix = "m", public_ = true),
    })
    @Data
    private static class TestArticle extends AbstractEntityModel {

        final static Map<String, SemanticField> SEMANTIC_FIELDS = new ImmutableMap.Builder<String, SemanticField>()
                .put("headline", semanticField("headline", "/TestArticle/headline"))

                .put("emptyField", semanticField("emptyField", "/TestArticle/emptyField"))

                .put("articleBody", semanticField("articleBody", "/TestArticle/articleBody",
                        new SemanticMapBuilder().sdlCore("TestParagraph", "subheading", TestParagraph.SEMANTIC_FIELDS.get("subheading")).build(), true))

                .put("date", semanticField("date", "/TestArticle/date"))

                .put("dateCreated", semanticField("dateCreated", "/TestArticle/dateCreated"))

                .put("standardMeta", semanticField("standardMeta", "/Metadata/standardMeta",
                        new SemanticMapBuilder()
                                .sdlCore("StandardMetadata", "date", semanticField("date", "/Metadata/standardMeta/dateCreated"))
                                .build(), false))

                .put("manyMappings", semanticField("manyMappings", "/TestArticle/manyMappings"))
                .put("sMapping1", semanticField("mapping1", "/TestArticle/mapping1"))
                .put("mMapping2", semanticField("mapping2", "/MyTestArticle/mapping2"))

                .put("manyMappingsNoValue", semanticField("manyMappingsNoValue", "/TestArticle/manyMappingsNoValue"))
                .put("sMapping1_nv", semanticField("mapping1", "/TestArticle/mapping1_nv"))
                .put("mMapping2_nv", semanticField("mapping2", "/MyTestArticle/mapping2_nv"))

                .build();

        @SemanticProperty("s:headline")
        private String headline;

        private String emptyField;

        @SemanticProperties({
                @SemanticProperty("s:mapping1"),
                @SemanticProperty("m:mapping2")
        })
        private String manyMappings;

        @SemanticProperties({
                @SemanticProperty("s:mapping1_nv"),
                @SemanticProperty("m:mapping2_nv")
        })
        private String manyMappingsNoValue;

        @SemanticProperty("s:articleBody")
        private List<TestParagraph> articleBody;

        private DateTime date;

        static Map<FieldSemantics, SemanticField> getSemantics() {

            return new SemanticMapBuilder()
                    .both("TestArticle", "headline", SEMANTIC_FIELDS.get("headline"))

                    .sdlCore("TestArticle", "emptyField", SEMANTIC_FIELDS.get("emptyField"))

                    .sdlCore("TestArticle", "manyMappings", SEMANTIC_FIELDS.get("manyMappings"))
                    .schemaOrg("TestArticle", "mapping1", SEMANTIC_FIELDS.get("sMapping1"))
                    .schemaOrg("MyTestArticle", "mapping2", SEMANTIC_FIELDS.get("mMapping2"))

                    .sdlCore("TestArticle", "manyMappingsNoValue", SEMANTIC_FIELDS.get("manyMappingsNoValue"))
                    .schemaOrg("TestArticle", "mapping1_nv", SEMANTIC_FIELDS.get("sMapping1_nv"))
                    .schemaOrg("MyTestArticle", "mapping2_nv", SEMANTIC_FIELDS.get("mMapping2_nv"))

                    .both("TestArticle", "articleBody", SEMANTIC_FIELDS.get("articleBody"))

                    .sdlCore("TestArticle", "date", SEMANTIC_FIELDS.get("date"))

                    .sdlCore("TestArticle", "standardMeta", SEMANTIC_FIELDS.get("standardMeta"))

                    .build();
        }
    }
}

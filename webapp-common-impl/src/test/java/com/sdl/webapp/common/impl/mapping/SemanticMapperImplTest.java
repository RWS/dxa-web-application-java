package com.sdl.webapp.common.impl.mapping;

import com.sdl.webapp.common.api.mapping.FieldData;
import com.sdl.webapp.common.api.mapping.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.Article;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG_VOCABULARY;
import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE_VOCABULARY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@code SemanticMapperImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SemanticMapperImplTestConfig.class)
public class SemanticMapperImplTest {

    private static final Map<FieldSemantics, SemanticField> EMPTY_FIELDS_MAP = Collections.emptyMap();

    @Autowired
    private SemanticMapperImpl semanticMapper;

    @Test
    public void testCreateArticle() throws SemanticMappingException, NoSuchFieldException {
        final Map<FieldSemantics, SemanticField> schemaFields = new HashMap<>();

        final SemanticField headlineField = new SemanticField("headline", "/Article/headline", false, EMPTY_FIELDS_MAP);
        schemaFields.put(new FieldSemantics(SDL_CORE_VOCABULARY, "Article", "headline"), headlineField);
        schemaFields.put(new FieldSemantics(SCHEMA_ORG_VOCABULARY, "Article", "headline"), headlineField);

        final SemanticField imageField = new SemanticField("image", "/Article/image", false, EMPTY_FIELDS_MAP);
        schemaFields.put(new FieldSemantics(SDL_CORE_VOCABULARY, "Article", "image"), imageField);
        schemaFields.put(new FieldSemantics(SCHEMA_ORG_VOCABULARY, "Article", "image"), imageField);

        final Map<FieldSemantics, SemanticField> articleBodyFields = new HashMap<>();
        final SemanticField subheadingField = new SemanticField("subheading", "/Article/articleBody/subheading", false, EMPTY_FIELDS_MAP);
        articleBodyFields.put(new FieldSemantics(SDL_CORE_VOCABULARY, "Paragraph", "subheading"), subheadingField);
        final SemanticField contentField = new SemanticField("content", "/Article/articleBody/content", false, EMPTY_FIELDS_MAP);
        articleBodyFields.put(new FieldSemantics(SDL_CORE_VOCABULARY, "Paragraph", "content"), contentField);
        final SemanticField mediaField = new SemanticField("media", "/Article/articleBody/media", false, EMPTY_FIELDS_MAP);
        articleBodyFields.put(new FieldSemantics(SDL_CORE_VOCABULARY, "Paragraph", "media"), mediaField);
        final SemanticField captionField = new SemanticField("caption", "/Article/articleBody/caption", false, EMPTY_FIELDS_MAP);
        articleBodyFields.put(new FieldSemantics(SDL_CORE_VOCABULARY, "Paragraph", "caption"), captionField);

        final SemanticField articleBodyField1 = new SemanticField("articleBody", "/Article/articleBody", true, articleBodyFields);
        schemaFields.put(new FieldSemantics(SDL_CORE_VOCABULARY, "Article", "articleBody"), articleBodyField1);
        final SemanticField articleBodyField2 = new SemanticField("articleBody", "/Article/articleBody", true, EMPTY_FIELDS_MAP);
        schemaFields.put(new FieldSemantics(SCHEMA_ORG_VOCABULARY, "Article", "articleBody"), articleBodyField2);

        final Map<FieldSemantics, SemanticField> standardMetaFields = new HashMap<>();
        final SemanticField dateCreatedField = new SemanticField("dateCreated", "/Metadata/standardMeta/dateCreated", false, EMPTY_FIELDS_MAP);
        standardMetaFields.put(new FieldSemantics(SDL_CORE_VOCABULARY, "StandardMetadata", "dateCreated"), dateCreatedField);
        standardMetaFields.put(new FieldSemantics(SCHEMA_ORG_VOCABULARY, "Article", "dateCreated"), dateCreatedField);
        final SemanticField descriptionField = new SemanticField("description", "/Metadata/standardMeta/description", false, EMPTY_FIELDS_MAP);
        standardMetaFields.put(new FieldSemantics(SDL_CORE_VOCABULARY, "StandardMetadata", "description"), descriptionField);
        final SemanticField nameField = new SemanticField("name", "/Metadata/standardMeta/name", false, EMPTY_FIELDS_MAP);
        standardMetaFields.put(new FieldSemantics(SDL_CORE_VOCABULARY, "StandardMetadata", "name"), nameField);
        final SemanticField introTextField = new SemanticField("introText", "/Metadata/standardMeta/introText", false, EMPTY_FIELDS_MAP);
        standardMetaFields.put(new FieldSemantics(SDL_CORE_VOCABULARY, "StandardMetadata", "introText"), introTextField);
        final SemanticField authorField = new SemanticField("author", "/Metadata/standardMeta/author", false, EMPTY_FIELDS_MAP);
        standardMetaFields.put(new FieldSemantics(SDL_CORE_VOCABULARY, "StandardMetadata", "author"), authorField);

        final SemanticField standardMetaField = new SemanticField("standardMeta", "/Metadata/standardMeta", false, standardMetaFields);
        schemaFields.put(new FieldSemantics(SDL_CORE_VOCABULARY, "Article", "standardMeta"), standardMetaField);

        final SemanticFieldDataProvider fieldDataProvider = mock(SemanticFieldDataProvider.class);

        when(fieldDataProvider.getFieldData(headlineField, new TypeDescriptor(Article.class.getDeclaredField("headline"))))
                .thenReturn(new FieldData("HEADLINE", "HeadlineField"));

        final DateTime dateTime = new DateTime(2014, 11, 4, 13, 14, 34, 123, DateTimeZone.UTC);
        when(fieldDataProvider.getFieldData(dateCreatedField, new TypeDescriptor(Article.class.getDeclaredField("date"))))
                .thenReturn(new FieldData(dateTime, "DateCreatedField"));

        final Article article = semanticMapper.createEntity(Article.class, schemaFields, fieldDataProvider);

        assertThat(article.getHeadline(), is("HEADLINE"));
        assertThat(article.getDate(), is(dateTime));

        final Map<String, String> propertyData = article.getPropertyData();
        assertThat(propertyData.entrySet(), hasSize(2));
        assertThat(propertyData, hasEntry("headline", "HeadlineField"));
        assertThat(propertyData, hasEntry("date", "DateCreatedField"));
    }
}

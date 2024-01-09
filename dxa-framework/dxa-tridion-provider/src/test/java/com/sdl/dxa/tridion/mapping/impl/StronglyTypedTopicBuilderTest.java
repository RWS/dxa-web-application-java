package com.sdl.dxa.tridion.mapping.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.sdl.dxa.DxaSpringInitialization;
import com.sdl.dxa.IshModuleInitializer;
import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.MvcModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.api.datamodel.model.RegionModelData;
import com.sdl.dxa.tridion.mapping.EntityModelBuilder;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.PageModelBuilder;
import com.sdl.dxa.tridion.models.entity.Article;
import com.sdl.dxa.tridion.models.topic.SpecializedBodyTest;
import com.sdl.dxa.tridion.models.topic.SpecializedSectionTest;
import com.sdl.dxa.tridion.models.topic.SpecializedTopicTest;
import com.sdl.dxa.tridion.models.topic.StronglyTypedTopicTest;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.api.mapping.views.AbstractModuleInitializer;
import com.sdl.webapp.common.api.mapping.views.RegisteredViewModel;
import com.sdl.webapp.common.api.mapping.views.RegisteredViewModels;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.GenericTopic;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.impl.mapping.SemanticMapperImpl;
import com.sdl.webapp.common.impl.mapping.SemanticMappingRegistryImpl;
import com.sdl.webapp.common.impl.model.ViewModelRegistryImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultModelBuilderTest.SpringConfigurationContext.class, StronglyTypedTopicBuilderTest.SpringConfigurationContext.class})
@ActiveProfiles("test")
public class StronglyTypedTopicBuilderTest {
    private ObjectMapper objectMapper;

    @Autowired
    private ModelBuilderPipeline modelBuilderPipeline;

    @Autowired
    StronglyTypedTopicBuilder stronglyTypedTopicBuilder;

    @BeforeEach
    public void setup() {
        DxaSpringInitialization spring = new DxaSpringInitialization();
        objectMapper = spring.objectMapper();
    }

    @Test
    public void BuildEntityModel_Null_Exception() throws DxaException {
        Assertions.assertThrows(DxaException.class, () -> {
            // This would happen if you accidentally configure this model builder as the first in the pipeline.
            EntityModel testEntityModel = null;
            stronglyTypedTopicBuilder.buildEntityModel(testEntityModel, null, null);
        });
    }

    @Test
    public void BuildEntityModel_NotTopic_Success() throws DxaException {
        Article testArticle = mock(Article.class);

        EntityModel testEntityModel = testArticle;
        stronglyTypedTopicBuilder.buildEntityModel(testEntityModel, null, null);

        assertEquals("testEntityModel", testArticle, testEntityModel);
    }

    @Test
    public void BuildEntityModel_NoMatchingStronglyTypedTopic_Success() throws DxaException {
        GenericTopic genericTopic = new GenericTopic();
        genericTopic.setTopicBody(null);

        EntityModel testEntityModel = genericTopic;
        stronglyTypedTopicBuilder.buildEntityModel(testEntityModel, null, null);

        assertEquals("testEntityModel", genericTopic, testEntityModel);
    }

    @Test
    public void BuildEntityModel_TopicBodyIllFormedXml_Success() throws DxaException {
        GenericTopic genericTopic = new GenericTopic("TopicBody", "<IllFormedXML>");

        EntityModel testEntityModel = genericTopic;
        stronglyTypedTopicBuilder.buildEntityModel(testEntityModel, null, null);

        assertEquals("testEntityModel", genericTopic, testEntityModel);
    }

    @Test
    public void BuildEntityModel_TitleBodySections_Success() throws DxaException {
        String testTitle = "DITA title";
        String testBody = "<div class=\"section \">First section</div><div class=\"section \">Second section</div>";
        GenericTopic genericTopic = new GenericTopic(
                "<Test topic title>",
                "<h1 class=\"title \">" + testTitle + "</h1><div class=\"body\">" + testBody + "</div>");

        EntityModel testEntityModel = stronglyTypedTopicBuilder.buildEntityModel(genericTopic, null, null);

        outputJson(testEntityModel);

        assertTrue("result", testEntityModel instanceof StronglyTypedTopicTest);
        StronglyTypedTopicTest result = (StronglyTypedTopicTest) testEntityModel;
        assertEquals("result.TopicTitle", genericTopic.getTopicTitle(), result.getTopicTitle());
        assertEquals("result.title", testTitle, result.getTitle());
        assertEquals("result.body", "First sectionSecond section", result.getBody()); // HTML tags should get stripped ("InnerText")
        assertNotNull("result.BodyRichText", result.getBodyRichText());
        assertEquals("result.BodyRichText.toString()", testBody, result.getBodyRichText().toString());
        assertEquals("result.FirstSection", "First section", result.getFirstSection());
        assertNotNull("result.sections", result.getSections());
        assertEquals("result.sections.Count", 2, result.getSections().size());
        assertEquals("result.sections[0]", result.getFirstSection(), result.getSections().get(0));
        assertNull("result.Links", result.getLinks());
        assertNull("result.FirstChildLink", result.getFirstChildLink());
        assertNull("result.ChildLinks", result.getChildLinks());
        assertNotNull("result.MvcData", result.getMvcData());
        assertEquals("result.MvcData", result.getDefaultMvcData(), result.getMvcData());
    }

    @Test
    public void BuildEntityModel_ThroughModelBuilderPipeline_Success() {
        String testTopicId = "1612-1970";
        String testTitle = "DITA title";
        String testBody = "<div class=\"section \">First section</div><div class=\"section \">Second section</div>";
        GenericTopic genericTopic = new GenericTopic(
                "<Test topic title>",
                "<h1 class=\"title \">" + testTitle + "</h1><div class=\"body \">" + testBody + "</div>"
        );

        ArrayList<EntityModelData> entities = new ArrayList<>();
        EntityModelData entityModelData = new EntityModelData();
        entityModelData.setId(testTopicId);
        entityModelData.setSchemaId("1");// Tridion Docs uses a hard-coded/fake Schema ID.
        ContentModelData contentModelData = new ContentModelData();
        contentModelData.put("topicTitle", genericTopic.getTopicTitle());
        contentModelData.put("topicBody", genericTopic.getTopicBody());
        entityModelData.setContent(contentModelData);
        entityModelData.setMvcData(MvcModelData.builder().areaName("Ish").viewName("Topic").build());
        entities.add(entityModelData);

        List regions = new ArrayList<RegionModelData>();
        RegionModelData regionModelData = new RegionModelData();
        regionModelData.setName("Main");
        regionModelData.setEntities(entities);
        regionModelData.setMvcData(MvcModelData.builder().areaName("Test").viewName("Main").build());
        regions.add(regionModelData);

        PageModelData testPageModelData = new PageModelData("666", null, null, null, null, null, regions, null);
        testPageModelData.setMvcData(MvcModelData.builder().areaName("Test").viewName("SimpleTestPage").build());

        PageModel pageModel = modelBuilderPipeline.createPageModel(testPageModelData);
        assertNotNull("pageModel", pageModel);

        outputJson(pageModel);

        StronglyTypedTopicTest result = (StronglyTypedTopicTest) pageModel.getRegions().get("Main").getEntities().get(0);
        assertNotNull("result", result);

        assertEquals("result.Id", testTopicId, result.getId());
        assertEquals("result.TopicTitle", genericTopic.getTopicTitle(), result.getTopicTitle());
        assertEquals("result.title", testTitle, result.getTitle());
        assertEquals("result.BodyRichText.toString()", testBody, result.getBodyRichText().toString());
        assertEquals("result.FirstSection", "First section", result.getFirstSection());
        assertEquals("result.MvcData", result.getDefaultMvcData(), result.getMvcData());
    }

    @Test
    public void TryConvertToStronglyTypedTopic_Links_Success() throws DxaException {
        GenericTopic genericTopic = new GenericTopic(
                "16121970",
                "<div class=\"body \" /><div class=\"related-links \">" +
                        "<div class=\"childlink \"><strong><a class=\"link \" href=\"/firstlink.html\">First link text</a></strong></div>" +
                        "<div class=\"childlink \"><strong><a class=\"link \" href=\"/secondlink.html\">Second link text</a></strong></div>" +
                        "<div class=\"parentlink \"><strong><a class=\"link \" href=\"/thirdlink.html\">Third link text</a></strong></div>" +
                        "</div>"
        );

        StronglyTypedTopicTest result = stronglyTypedTopicBuilder.tryConvertToStronglyTypedTopic(genericTopic, StronglyTypedTopicTest.class);
        assertNotNull("result", result);

        outputJson(result);

        assertEquals("result.Id", genericTopic.getId(), result.getId());
        assertNull("result.title", result.getTitle());
        assertEquals("result.body", "", result.getBody());
        assertNotNull("result.Links", result.getLinks());
        assertEquals("result.Links.Count", 3, result.getLinks().size());
        assertNotNull("result.FirstChildLink", result.getFirstChildLink());
        assertEquals("result.FirstChildLink.Url", "/firstlink.html", result.getFirstChildLink().getUrl());
        assertEquals("result.FirstChildLink.LinkText", "First link text", result.getFirstChildLink().getLinkText());
        assertNull("result.FirstChildLink.AlternateText", result.getFirstChildLink().getAlternateText());
        assertNotNull("result.ChildLinks", result.getChildLinks());
        assertEquals("result.ChildLinks.Count", 2, result.getChildLinks().size());
    }

    @Test
    public void TryConvertToStronglyTypedTopic_SpecializedTopic_Success() throws DxaException {
        String testTitle = "DITA title";
        String testBody = "<div class=\"section lcIntro \" id=\"s1\">Intro section</div><div class=\"section lcObjectives \" id=\"s2\">Objectives section</div>";
        GenericTopic genericTopic = new GenericTopic(
                "Specialized topic title",
                "<h1 class=\"title \">" + testTitle + "</h1><div class=\"body lcBaseBody lcOverviewBody \" id=\"b1\">" + testBody + " </div>"
        );

        SpecializedTopicTest result = stronglyTypedTopicBuilder.tryConvertToStronglyTypedTopic(genericTopic, SpecializedTopicTest.class);
        assertNotNull("result", result);

        outputJson(result);

        assertNotNull("result.intro", result.getIntro());
        assertNotNull("result.objectives", result.getObjectives());
        assertNotNull("result.body", result.getBody());
        assertNotNull("result.body.intro", result.getBody().getIntro());
        assertNotNull("result.body.objectives", result.getBody().getObjectives());
        assertNotNull("result.body.objectives.content", result.getBody().getObjectives().getContent());
        assertNotNull("result.body.sections", result.getBody().getSections());

        assertEquals("result.intro.toString()", "Intro section", result.getIntro().toString());
        assertEquals("result.objectives.toString()", "Objectives section", result.getObjectives().toString());

        assertEquals("result.body.Id", "b1", result.getBody().getId());
        assertEquals("body lcBaseBody lcOverviewBody ", result.getBody().getHtmlClasses(), "body lcBaseBody lcOverviewBody ");
        assertEquals("result.body.intro.toString()", "Intro section", result.getBody().getIntro().toString());

        assertEquals("result.body.objectives.Id", "s2", result.getBody().getObjectives().getId());
        assertEquals("result.body.objectives.HtmlClasses", "section lcObjectives ", result.getBody().getObjectives().getHtmlClasses());
        assertEquals("result.body.objectives.content.toString()", "Objectives section", result.getBody().getObjectives().getContent().toString());

        assertEquals("result.body.sections.Count", 2, result.getBody().getSections().size());
        assertEquals("result.body.sections[0].Id", "s1", result.getBody().getSections().get(0).getId());
        assertEquals("result.body.sections[1].Id", "s2", result.getBody().getSections().get(1).getId());
        assertEquals("result.body.sections[0].HtmlClasses", "section lcIntro ", result.getBody().getSections().get(0).getHtmlClasses());
        assertEquals("result.body.sections[1].HtmlClasses", "section lcObjectives ", result.getBody().getSections().get(1).getHtmlClasses());

        assertNotNull("result.MvcData", result.getMvcData());
        assertEquals("result.MvcData", result.getDefaultMvcData(), result.getMvcData());
        assertNotNull("result.body.MvcData", result.getBody().getMvcData());
        assertEquals("result.body.MvcData", result.getBody().getDefaultMvcData(), result.getBody().getMvcData());

    }

    private void outputJson(Object objectToSerialize) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(objectToSerialize);
            System.out.println("---- JSON Representation of " + objectToSerialize.getClass().getName() + "----");
            System.out.println(json);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Configuration
    public static class SpringConfigurationContext {

        @Bean
        public StronglyTypedTopicBuilder stronglyTypedTopicBuilder() throws XPathExpressionException {
            StronglyTypedTopicBuilder result = new StronglyTypedTopicBuilder();
            return result;
        }

        @Bean
        public List<EntityModelBuilder> entityModelBuilders(StronglyTypedTopicBuilder stronglyTypedTopicBuilder, DefaultModelBuilder defaultModelBuilder) {
            return Lists.newArrayList(defaultModelBuilder, stronglyTypedTopicBuilder);
        }

        @Bean
        public List<PageModelBuilder> pageModelBuilders(DefaultModelBuilder defaultModelBuilder) {
            return Lists.newArrayList(defaultModelBuilder);
        }

        @Bean
        public SemanticMapper semanticMapper(SemanticMappingRegistry semanticMappingRegistry) {
            return new SemanticMapperImpl(semanticMappingRegistry);
        }

        @Bean
        public WebRequestContext webRequestContext() {
            WebRequestContext mock = mock(WebRequestContext.class);
            Localization localization = localization();
            when(mock.getLocalization()).thenReturn(localization);

            SemanticSchema semanticSchema = new SemanticSchema(1, null, new HashSet<>(), getSemanticMappings());
            Map<Long, SemanticSchema> kvMap = Collections.singletonMap(1l, semanticSchema);
            when(localization.getSemanticSchemas()).thenReturn(kvMap);
            String id = "1081";
            String path = "/autotest-parent-legacy";
            when(localization.getId()).thenReturn(id);
            when(localization.getPath()).thenReturn(path);

            return mock;
        }

        private Map<FieldSemantics, SemanticField> getSemanticMappings() {
            Map<FieldSemantics, SemanticField> result = new HashMap<>();

            SemanticField titleSF = new SemanticField("title", "/GenericTopic/topicTitle", false, Collections.EMPTY_MAP);
            SemanticField topicSF = new SemanticField("topic", "/GenericTopic/topicBody", false, Collections.EMPTY_MAP);

            result.put(new FieldSemantics(SemanticVocabulary.SDL_CORE_VOCABULARY, "Topic", "topicTitle"), titleSF);
            result.put(new FieldSemantics(SemanticVocabulary.SDL_CORE_VOCABULARY, "Topic", "topicBody"), topicSF);

            return result;
        }

        @Bean
        public Localization localization() {
            Localization localization = mock(Localization.class);

            return localization;
        }

        @Bean
        public ViewModelRegistry viewModelRegistry() {
            ViewModelRegistry viewModelRegistry = new ViewModelRegistryImpl();
            return viewModelRegistry;
        }

        @Bean
        public IshModuleInitializer ishModuleInitializer() {
            return new IshModuleInitializer();
        }

        @Bean
        public TestClassInitializer testClassInitializer() {
            return new TestClassInitializer();
        }

        @RegisteredViewModels({
                @RegisteredViewModel(modelClass = Link.class),
                @RegisteredViewModel(modelClass = SpecializedSectionTest.class),
                @RegisteredViewModel(modelClass = SpecializedBodyTest.class),
                @RegisteredViewModel(modelClass = StronglyTypedTopicTest.class),
                @RegisteredViewModel(viewName = "SimpleTestPage", modelClass = DefaultPageModel.class), //, controllerName = "Page"),
                @RegisteredViewModel(viewName = "Main", modelClass = RegionModelImpl.class),
                @RegisteredViewModel(modelClass = SpecializedTopicTest.class)
        })
        private static class TestClassInitializer extends AbstractModuleInitializer {

            @Override
            protected String getAreaName() {
                return "Test";
            }
        }

        @Bean
        public SemanticMappingRegistry semanticMappingRegistry() {
            SemanticMappingRegistry semanticMappingRegistry = new SemanticMappingRegistryImpl();

            return semanticMappingRegistry;
        }
    }

}

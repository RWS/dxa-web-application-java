package com.sdl.dxa.tridion.mapping.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.sdl.dxa.api.datamodel.DataModelSpringConfiguration;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.tridion.mapping.PageInclusion;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.semantic.config.EntitySemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.mapping.views.AbstractInitializer;
import com.sdl.webapp.common.api.mapping.views.RegisteredViewModel;
import com.sdl.webapp.common.api.mapping.views.RegisteredViewModels;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import com.sdl.webapp.common.impl.mapping.SemanticMapperImpl;
import com.sdl.webapp.common.impl.mapping.SemanticMappingRegistryImpl;
import com.sdl.webapp.common.impl.model.ViewModelRegistryImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.IOException;
import java.util.Collections;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE_VOCABULARY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class DefaultModelBuilderTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DefaultModelBuilder modelBuilder;

    @Autowired
    private Localization localization;

    @Test
    @Ignore
    public void shouldBuildPageModel_OutOfModelDataR2() throws IOException {
        //given
        PageModelData pageModelData = objectMapper.readValue(new ClassPathResource("home_page_json_full.json").getFile(), PageModelData.class);

        //when
        PageModel pageModel = modelBuilder.buildPageModel(null, pageModelData, PageInclusion.INCLUDE, localization);

        //then
        assertEqualsAndNotNull(pageModelData.getId(), pageModel.getId());
        assertEqualsAndNotNull(pageModelData.getTitle(), pageModel.getTitle());
//        ((ItemList) pageModel.getRegions().get("Hero").getEntity("1472"))
    }

    private void assertEqualsAndNotNull(Object expected, Object actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class TestEntity extends AbstractEntityModel {

        private String headline;
    }

    @RegisteredViewModels({
            @RegisteredViewModel(modelClass = TestEntity.class),
            @RegisteredViewModel(modelClass = DefaultPageModel.class, viewName = "GeneralPage")
    })
    private static class TestClassInitializer extends AbstractInitializer {

        @Override
        protected String getAreaName() {
            return "TestClass";
        }
    }

    @Configuration
    public static class SpringConfigurationContext {

        @Bean
        public Localization localization() {
            Localization localization = mock(Localization.class);
            when(localization.getSemanticSchemas()).thenReturn(ImmutableMap.<Long, SemanticSchema>builder()
                    .put(10015L, new SemanticSchema(10015L, "NotImportant", Collections.emptySet(), Collections.emptyMap()))
                    .put(2737L, new SemanticSchema(2737L, "TestEntity",
                            Sets.newHashSet(new EntitySemantics(SDL_CORE_VOCABULARY, "TestEntity")),
                            ImmutableMap.of(
                                    new FieldSemantics(SDL_CORE_VOCABULARY, "TestEntity", "headline"),
                                    new SemanticField("headline", "/TestEntity/headline", false, Collections.emptyMap())
                            ))).build());

            return localization;
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new DataModelSpringConfiguration().dxaR2ObjectMapper();
        }

        @Bean
        public DefaultModelBuilder defaultModelBuilder() {
            return new DefaultModelBuilder();
        }

        @Bean
        public TestClassInitializer testClassInitializer() {
            return new TestClassInitializer();
        }

        @Bean
        public ViewModelRegistry viewModelRegistryImpl() {
            return new ViewModelRegistryImpl();
        }

        @Bean
        public SemanticMapper semanticMapper() {
            return new SemanticMapperImpl(semanticMappingRegistry());
        }

        @Bean
        public SemanticMappingRegistry semanticMappingRegistry() {
            return new SemanticMappingRegistryImpl();
        }
    }

}
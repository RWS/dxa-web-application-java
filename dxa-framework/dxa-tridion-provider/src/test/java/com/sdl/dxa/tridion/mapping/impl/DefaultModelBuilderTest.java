package com.sdl.dxa.tridion.mapping.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.sdl.dxa.api.datamodel.DataModelSpringConfiguration;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.api.datamodel.model.RegionModelData;
import com.sdl.dxa.api.datamodel.model.ViewModelData;
import com.sdl.dxa.caching.LocalizationAwareKeyGenerator;
import com.sdl.dxa.caching.LocalizationIdProvider;
import com.sdl.dxa.caching.NamedCacheProvider;
import com.sdl.dxa.caching.WebRequestContextLocalizationIdProvider;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.converter.GenericSemanticModelDataConverter;
import com.sdl.dxa.tridion.mapping.converter.StringModelConverter;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.semantic.config.EntitySemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.mapping.views.AbstractModuleInitializer;
import com.sdl.webapp.common.api.mapping.views.RegisteredViewModel;
import com.sdl.webapp.common.api.mapping.views.RegisteredViewModels;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.impl.mapping.SemanticMapperImpl;
import com.sdl.webapp.common.impl.mapping.SemanticMappingRegistryImpl;
import com.sdl.webapp.common.impl.model.ViewModelRegistryImpl;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.cache.Cache;
import java.util.Collections;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE_VOCABULARY;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class DefaultModelBuilderTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DefaultModelBuilder modelBuilder;

    @Test
    public void shouldBuildPageModel_OutOfModelDataR2() throws Exception {
        //given
        PageModelData pageModelData = objectMapper.readValue(new ClassPathResource("home_page_json_full.json").getFile(), PageModelData.class);

        //when
        PageModel pageModel = modelBuilder.buildPageModel(null, pageModelData);

        //then
        // page.Id
        assertEquals("640", pageModel.getId());

        // page.Title
        assertEquals("Home", pageModel.getName());
        assertEquals(pageModelData.getTitle() + "|My Site", pageModel.getTitle());

        // page.Meta
        assertEquals("000 Home", pageModel.getMeta().get("sitemapKeyword"));
        assertEquals("<p>text<a href=\"resolved-link\">text</a></p>", pageModel.getMeta().get("richText"));

        // region (0). region (0) -> Header region
        RegionModelData regionModelData = pageModelData.getRegions().get(0);
        RegionModel headerRegion = pageModel.getRegions().get("Header");
        assertEqualsAndNotNull(regionModelData.getName(), headerRegion.getName());

        // Header region (0) -> Info sub-region
        RegionModelData subRegionModelData = regionModelData.getRegions().get(0);
        RegionModel infoRegion = headerRegion.getRegions().get("Info");
        assertEqualsAndNotNull(subRegionModelData.getName(), infoRegion.getName());

        // Info sub-region -> entity (0)
        EntityModelData entityModelData = subRegionModelData.getEntities().get(0);
        EntityModel infoRegionEntities = infoRegion.getEntities().get(0);
        assertEqualsAndNotNull(entityModelData.getId(), infoRegionEntities.getId());

        assertEquals(2, infoRegion.getEntities().size());

        // region(0).region(0).entity(0).MvcData
        assertEqualsAndNotNull(entityModelData.getMvcData().getViewName(), infoRegionEntities.getMvcData().getViewName());

        // region(0).region(0).entity(0).xpmMetadata
        assertXpmMetadata(entityModelData, infoRegionEntities, "ComponentID");
        assertXpmMetadata(entityModelData, infoRegionEntities, "ComponentModified");
        assertXpmMetadata(entityModelData, infoRegionEntities, "ComponentTemplateID");
        assertXpmMetadata(entityModelData, infoRegionEntities, "ComponentTemplateModified");
        assertXpmMetadata(entityModelData, infoRegionEntities, "IsRepositoryPublished");

        // region(0).region(0).MvcData
        assertEqualsAndNotNull(subRegionModelData.getMvcData().getViewName(), infoRegion.getMvcData().getViewName());

        // region(0).MvcData
        assertEqualsAndNotNull(regionModelData.getMvcData().getViewName(), headerRegion.getMvcData().getViewName());

        // region(0).XpmMetadata
        assertXpmMetadata(regionModelData, headerRegion, "IncludedFromPageId");
        assertXpmMetadata(regionModelData, headerRegion, "IncludedFromPageTitle");
        assertXpmMetadata(regionModelData, headerRegion, "IncludedFromPageFileName");

        // page.getMvcData
        assertEqualsAndNotNull(pageModelData.getMvcData().getViewName(), pageModel.getMvcData().getViewName());

        // page.getXpmMetadata
        assertXpmMetadata(pageModelData, pageModel, "PageID");
        assertXpmMetadata(pageModelData, pageModel, "PageModified");
        assertXpmMetadata(pageModelData, pageModel, "PageTemplateID");
        assertXpmMetadata(pageModelData, pageModel, "PageTemplateModified");
    }

    private void assertEqualsAndNotNull(Object expected, Object actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    public void assertXpmMetadata(ViewModelData modelData, ViewModel model, String key) {
        assertEqualsAndNotNull(modelData.getXpmMetadata().get(key), model.getXpmMetadata().get(key));
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class TestEntity extends AbstractEntityModel {

        private String headline;
    }

    @RegisteredViewModels({
            @RegisteredViewModel(modelClass = TestEntity.class, viewName = "TestClassView"),
            @RegisteredViewModel(modelClass = DefaultPageModel.class, viewName = "GeneralPage"),
            @RegisteredViewModel(viewName = "Header", modelClass = RegionModelImpl.class),
            @RegisteredViewModel(viewName = "Info", modelClass = RegionModelImpl.class)
    })
    private static class TestClassInitializer extends AbstractModuleInitializer {

        @Override
        protected String getAreaName() {
            return "Core";
        }
    }

    @Configuration
    @Profile("test")
    public static class SpringConfigurationContext {

        @Bean
        public LocalizationIdProvider webRequestContextLocalizationIdProvider() {
            return new WebRequestContextLocalizationIdProvider();
        }

        @Bean
        public LocalizationAwareKeyGenerator localizationAwareKeyGenerator() {
            return spy(LocalizationAwareKeyGenerator.class);
        }

        @Bean
        public NamedCacheProvider namedCacheProvider() {
            NamedCacheProvider provider = mock(NamedCacheProvider.class);
            Cache cache = mock(Cache.class);
            when(provider.getCache(anyString(), any(), any())).thenReturn(cache);
            return provider;
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
        public SemanticMapper semanticMapper() {
            return new SemanticMapperImpl(semanticMappingRegistry());
        }

        @Bean
        public SemanticMappingRegistry semanticMappingRegistry() {
            return new SemanticMappingRegistryImpl();
        }

        @Bean
        public ApplicationContextHolder applicationContextHolder() {
            return new ApplicationContextHolder();
        }

        @Bean
        public GenericSemanticModelDataConverter sourceConverterFactory() {
            return new GenericSemanticModelDataConverter();
        }

        @Bean
        public LinkResolver linkResolver() {
            LinkResolver mock = mock(LinkResolver.class);
            when(mock.resolveLink(eq("tcm:1-2"), eq("1"), anyBoolean())).thenReturn("resolved-link");
            when(mock.resolveLink(eq("tcm:1-2"), eq("1"))).thenReturn("resolved-link");
            when(mock.resolveLink(eq("tcm:1-3"), eq("1"), anyBoolean())).thenReturn(null);
            when(mock.resolveLink(eq("tcm:1-3"), eq("1"))).thenReturn(null);
            return mock;
        }

        @Bean
        public WebRequestContext webRequestContext() {

            Localization localization = mock(Localization.class);
            when(localization.getSemanticSchemas()).thenReturn(ImmutableMap.<Long, SemanticSchema>builder()
                    .put(10015L, new SemanticSchema(10015L, "NotImportant", Collections.emptySet(), Collections.emptyMap()))
                    .put(2737L, new SemanticSchema(2737L, "TestEntity",
                            Sets.newHashSet(new EntitySemantics(SDL_CORE_VOCABULARY, "TestEntity")),
                            ImmutableMap.of(
                                    new FieldSemantics(SDL_CORE_VOCABULARY, "TestEntity", "headline"),
                                    new SemanticField("headline", "/TestEntity/headline", false, Collections.emptyMap())
                            ))).build());

            when(localization.getId()).thenReturn("1");
            when(localization.getResource(eq("core.pageTitleSeparator"))).thenReturn("|");
            when(localization.getResource(eq("core.pageTitlePostfix"))).thenReturn("My Site");

            WebRequestContext mock = mock(WebRequestContext.class);
            when(mock.getLocalization()).thenReturn(localization);
            return mock;
        }

        @Bean
        public StringModelConverter stringConverter() {
            return new StringModelConverter();
        }

        @Bean
        public ModelBuilderPipeline modelBuilderPipeline() {
            return new ModelBuilderPipelineImpl();
        }

        @Bean
        public ViewModelRegistry viewModelRegistryImpl() {
            return new ViewModelRegistryImpl();
        }
    }

}
package com.sdl.dxa.tridion.mapping.impl;

import com.google.common.collect.Lists;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.tridion.mapping.EntityModelBuilder;
import com.sdl.dxa.tridion.mapping.PageInclusion;
import com.sdl.dxa.tridion.mapping.PageModelBuilder;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Collections;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class ModelBuilderPipelineTest {

    private PageModelData pageModelData = new PageModelData("1", null, "title", null);

    private EntityModelData entityModelData = new EntityModelData("1", "url", null, null, null);

    private PageModel firstPageModel = new DefaultPageModel();

    private PageModel secondPageModel = new DefaultPageModel();

    private EntityModel firstEntityModel = mock(EntityModel.class);

    private EntityModel secondEntityModel = mock(EntityModel.class);

    @Autowired
    private PageModelBuilder firstPageModelBuilder;

    @Autowired
    private PageModelBuilder secondPageModelBuilder;

    @Autowired
    private EntityModelBuilder firstEntityModelBuilder;

    @Autowired
    private EntityModelBuilder secondEntityModelBuilder;

    @Autowired
    private Localization localization;

    @Autowired
    private ModelBuilderPipeline pipeline;

    @Before
    public void initMocks() {
        when(firstPageModelBuilder.buildPageModel(any(PageModel.class), any(PageModelData.class), any(PageInclusion.class), any(Localization.class)))
                .thenReturn(firstPageModel);
        when(secondPageModelBuilder.buildPageModel(any(PageModel.class), any(PageModelData.class), any(PageInclusion.class), any(Localization.class)))
                .thenReturn(secondPageModel);

        when(firstEntityModelBuilder.buildEntityModel(any(EntityModel.class), any(EntityModelData.class), any(Localization.class)))
                .thenReturn(firstEntityModel);
        when(secondEntityModelBuilder.buildEntityModel(any(EntityModel.class), any(EntityModelData.class), any(Localization.class)))
                .thenReturn(secondEntityModel);

        when(firstEntityModelBuilder.buildEntityModel(any(EntityModel.class), any(EntityModelData.class), any(Localization.class)))
                .thenReturn(firstEntityModel);
        when(secondEntityModelBuilder.buildEntityModel(any(EntityModel.class), any(EntityModelData.class), any(Localization.class)))
                .thenReturn(secondEntityModel);

        when(firstEntityModelBuilder.buildEntityModel(any(EntityModel.class), any(EntityModelData.class), anyObject(), any(Localization.class)))
                .thenReturn(firstEntityModel);
        when(secondEntityModelBuilder.buildEntityModel(any(EntityModel.class), any(EntityModelData.class), anyObject(), any(Localization.class)))
                .thenReturn(secondEntityModel);
    }

    @Test
    public void shouldIterate_AllPageModelBuilders() {
        //given
        PageInclusion pageInclusion = PageInclusion.INCLUDE;

        //when
        PageModel pageModel = pipeline.createPageModel(pageModelData, pageInclusion, localization);

        //then
        verify(firstPageModelBuilder).buildPageModel(isNull(PageModel.class), same(pageModelData), eq(pageInclusion), same(localization));
        verify(secondPageModelBuilder).buildPageModel(same(firstPageModel), same(pageModelData), eq(pageInclusion), same(localization));
        assertSame(secondPageModel, pageModel);
    }

    @Test
    public void shouldIterate_AllEntityModelBuilders() {
        //when
        EntityModel entityModel = pipeline.createEntityModel(entityModelData, localization);

        //then
        verify(firstEntityModelBuilder).buildEntityModel(isNull(EntityModel.class), same(entityModelData), same(localization));
        verify(secondEntityModelBuilder).buildEntityModel(same(firstEntityModel), same(entityModelData), same(localization));
        assertSame(secondEntityModel, entityModel);
    }

    @Test
    public void shouldIterate_AllEntityModelBuilders_WithClass() {
        //given
        Class<EntityModel> expectedClass = EntityModel.class;

        //when
        EntityModel entityModel = pipeline.createEntityModel(entityModelData, expectedClass, localization);

        //then
        verify(firstEntityModelBuilder).buildEntityModel(isNull(EntityModel.class), same(entityModelData), same(expectedClass), same(localization));
        verify(secondEntityModelBuilder).buildEntityModel(same(firstEntityModel), same(entityModelData), same(expectedClass), same(localization));
        assertSame(secondEntityModel, entityModel);
    }

    @Test
    public void shouldNotFail_IfListsOfBuildersNotSet() {
        //given 
        ModelBuilderPipeline pipeline = new ModelBuilderPipeline(null, null);

        //when
        PageModel pageModel = pipeline.createPageModel(pageModelData, PageInclusion.INCLUDE, localization);
        EntityModel entityModel = pipeline.createEntityModel(entityModelData, localization);

        //then
        assertNull(pageModel);
        assertNull(entityModel);
    }

    @Test
    public void shouldNotFail_IfListsOfBuildersIsEmpty() {
        //given
        ModelBuilderPipeline pipeline = new ModelBuilderPipeline(Collections.emptyList(), Collections.emptyList());

        //when
        PageModel pageModel = pipeline.createPageModel(pageModelData, PageInclusion.INCLUDE, localization);
        EntityModel entityModel = pipeline.createEntityModel(entityModelData, localization);

        //then
        assertNull(pageModel);
        assertNull(entityModel);
    }

    @Configuration
    @Profile("test")
    public static class SpringConfigurationContext {

        @Bean
        public ModelBuilderPipeline modelBuilderPipeline() {
            return new ModelBuilderPipeline(
                    Lists.newArrayList(firstEntityModelBuilder(), secondEntityModelBuilder()),
                    Lists.newArrayList(firstPageModelBuilder(), secondPageModelBuilder())
            );
        }

        @Bean
        public EntityModelBuilder firstEntityModelBuilder() {
            return mock(EntityModelBuilder.class);
        }

        @Bean
        public EntityModelBuilder secondEntityModelBuilder() {
            return mock(EntityModelBuilder.class);
        }

        @Bean
        public PageModelBuilder firstPageModelBuilder() {
            return mock(PageModelBuilder.class);
        }

        @Bean
        public PageModelBuilder secondPageModelBuilder() {
            return mock(PageModelBuilder.class);
        }

        @Bean
        public Localization localization() {
            return mock(Localization.class);
        }
    }

}
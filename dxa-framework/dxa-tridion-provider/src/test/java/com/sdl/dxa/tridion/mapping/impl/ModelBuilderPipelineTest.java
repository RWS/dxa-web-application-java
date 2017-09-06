package com.sdl.dxa.tridion.mapping.impl;

import com.google.common.collect.Lists;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.tridion.mapping.EntityModelBuilder;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.PageModelBuilder;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import com.sdl.webapp.common.exceptions.DxaException;
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

import java.util.Collections;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ModelBuilderPipelineTest.SpringConfigurationContext.class)
@ActiveProfiles("test")
public class ModelBuilderPipelineTest {

    private PageModelData pageModelData = new PageModelData("1", null, null, null, "title", null, null);

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
    private ModelBuilderPipeline pipeline;

    @Before
    public void initMocks() throws DxaException {
        when(firstPageModelBuilder.buildPageModel(any(PageModel.class), any(PageModelData.class)))
                .thenReturn(firstPageModel);
        when(secondPageModelBuilder.buildPageModel(any(PageModel.class), any(PageModelData.class)))
                .thenReturn(secondPageModel);

        when(firstEntityModelBuilder.buildEntityModel(any(EntityModel.class), any(EntityModelData.class), anyObject()))
                .thenReturn(firstEntityModel);
        when(secondEntityModelBuilder.buildEntityModel(any(EntityModel.class), any(EntityModelData.class), anyObject()))
                .thenReturn(secondEntityModel);

        when(firstEntityModelBuilder.buildEntityModel(any(EntityModel.class), any(EntityModelData.class), anyObject()))
                .thenReturn(firstEntityModel);
        when(secondEntityModelBuilder.buildEntityModel(any(EntityModel.class), any(EntityModelData.class), anyObject()))
                .thenReturn(secondEntityModel);

        when(firstEntityModelBuilder.buildEntityModel(any(EntityModel.class), any(EntityModelData.class), anyObject()))
                .thenReturn(firstEntityModel);
        when(secondEntityModelBuilder.buildEntityModel(any(EntityModel.class), any(EntityModelData.class), anyObject()))
                .thenReturn(secondEntityModel);
    }

    @Test
    public void shouldIterate_AllPageModelBuilders() {
        //given
        PageRequestDto.PageInclusion pageInclusion = PageRequestDto.PageInclusion.INCLUDE;

        //when
        PageModel pageModel = pipeline.createPageModel(pageModelData);

        //then
        verify(firstPageModelBuilder).buildPageModel(isNull(PageModel.class), same(pageModelData));
        verify(secondPageModelBuilder).buildPageModel(same(firstPageModel), same(pageModelData));
        assertSame(secondPageModel, pageModel);
    }

    @Test
    public void shouldIterate_AllEntityModelBuilders() throws DxaException {
        //when
        EntityModel entityModel = pipeline.createEntityModel(entityModelData);

        //then
        verify(firstEntityModelBuilder).buildEntityModel(isNull(EntityModel.class), same(entityModelData), anyObject());
        verify(secondEntityModelBuilder).buildEntityModel(same(firstEntityModel), same(entityModelData), anyObject());
        assertSame(secondEntityModel, entityModel);
    }

    @Test
    public void shouldIterate_AllEntityModelBuilders_WithClass() throws DxaException {
        //given
        Class<EntityModel> expectedClass = EntityModel.class;

        //when
        EntityModel entityModel = pipeline.createEntityModel(entityModelData, expectedClass);

        //then
        verify(firstEntityModelBuilder).buildEntityModel(isNull(EntityModel.class), same(entityModelData), same(expectedClass));
        verify(secondEntityModelBuilder).buildEntityModel(same(firstEntityModel), same(entityModelData), same(expectedClass));
        assertSame(secondEntityModel, entityModel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotFail_IfListsOfBuildersNotSet() throws DxaException {
        //given 
        ModelBuilderPipeline pipeline = new ModelBuilderPipelineImpl();

        //when
        PageModel pageModel = pipeline.createPageModel(pageModelData);
        EntityModel entityModel = pipeline.createEntityModel(entityModelData);

        //then
        //IAE
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFail_IfListsOfBuildersIsEmpty() throws DxaException {
        //given
        ModelBuilderPipelineImpl pipeline = new ModelBuilderPipelineImpl();
        pipeline.setEntityModelBuilders(Collections.emptyList());
        pipeline.setPageModelBuilders(Collections.emptyList());

        //when
        PageModel pageModel = pipeline.createPageModel(pageModelData);
        EntityModel entityModel = pipeline.createEntityModel(entityModelData);

        //then
        //IAE
    }

    @Configuration
    @Profile("test")
    public static class SpringConfigurationContext {

        @Bean
        public ModelBuilderPipeline modelBuilderPipeline() {
            ModelBuilderPipelineImpl pipeline = new ModelBuilderPipelineImpl();
            pipeline.setEntityModelBuilders(Lists.newArrayList(firstEntityModelBuilder(), secondEntityModelBuilder()));
            pipeline.setPageModelBuilders(Lists.newArrayList(firstPageModelBuilder(), secondPageModelBuilder()));
            return pipeline;
        }

        @Bean
        public EntityModelBuilder firstEntityModelBuilder() {
            EntityModelBuilder mock = mock(EntityModelBuilder.class);
            when(mock.getOrder()).thenReturn(1);
            return mock;
        }

        @Bean
        public EntityModelBuilder secondEntityModelBuilder() {
            EntityModelBuilder mock = mock(EntityModelBuilder.class);
            when(mock.getOrder()).thenReturn(2);
            return mock;
        }

        @Bean
        public PageModelBuilder firstPageModelBuilder() {
            PageModelBuilder mock = mock(PageModelBuilder.class);
            when(mock.getOrder()).thenReturn(1);
            return mock;
        }

        @Bean
        public PageModelBuilder secondPageModelBuilder() {
            PageModelBuilder mock = mock(PageModelBuilder.class);
            when(mock.getOrder()).thenReturn(2);
            return mock;
        }

        @Bean
        public WebRequestContext webRequestContext() {
            WebRequestContext mock = mock(WebRequestContext.class);
            when(mock.getLocalization()).thenReturn(localization());
            return mock;
        }
        
        @Bean
        public Localization localization() {
            return mock(Localization.class);
        }
    }

}
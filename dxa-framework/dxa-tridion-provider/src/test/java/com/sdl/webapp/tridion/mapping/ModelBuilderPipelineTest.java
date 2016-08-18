package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.page.PageModelImpl;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class ModelBuilderPipelineTest {

    @Autowired
    private EntityBuilder lowEntityBuilder;

    @Autowired
    private EntityBuilder highEntityBuilder;

    @Autowired
    private PageBuilder lowPageBuilder;

    @Autowired
    private PageBuilder highPageBuilder;

    @Autowired
    private Localization localization;

    @Autowired
    private ModelBuilderPipelineImpl modelBuilderPipeline;

    @Test
    public void shouldCallAllEntityBuilders1() throws ContentProviderException {
        //given
        TestEntity entity = new TestEntity();
        ComponentPresentation componentPresentation = mock(ComponentPresentation.class);

        when(lowEntityBuilder.createEntity(same(componentPresentation), any(EntityModel.class), same(localization))).thenReturn(entity);
        when(highEntityBuilder.createEntity(same(componentPresentation), any(EntityModel.class), same(localization))).thenReturn(entity);

        //when
        EntityModel result = modelBuilderPipeline.createEntityModel(componentPresentation, localization);

        //then
        verify(lowEntityBuilder).createEntity(same(componentPresentation), any(EntityModel.class), same(localization));
        verify(highEntityBuilder).createEntity(same(componentPresentation), any(EntityModel.class), same(localization));
        assertSame(entity, result);
    }

    @Test
    public void shouldCallAllEntityBuilders2() throws ContentProviderException {
        //given
        Component component = mock(Component.class);
        TestEntity entity = new TestEntity();

        when(lowEntityBuilder.createEntity(same(component), any(EntityModel.class), same(localization))).thenReturn(entity);
        when(highEntityBuilder.createEntity(same(component), any(EntityModel.class), same(localization))).thenReturn(entity);

        //when
        EntityModel result = modelBuilderPipeline.createEntityModel(component, localization);

        //then
        verify(lowEntityBuilder).createEntity(same(component), any(EntityModel.class), same(localization));
        verify(highEntityBuilder).createEntity(same(component), any(EntityModel.class), same(localization));
        assertSame(entity, result);
    }

    @Test
    public void shouldCallAllEntityBuilders3() throws ContentProviderException {
        //given
        Component component = mock(Component.class);
        TestEntity entity = new TestEntity();

        when(lowEntityBuilder.createEntity(same(component), any(EntityModel.class), same(localization), any(Class.class))).thenReturn(entity);
        when(highEntityBuilder.createEntity(same(component), any(EntityModel.class), same(localization), any(Class.class))).thenReturn(entity);

        //when
        EntityModel result = modelBuilderPipeline.createEntityModel(component, localization, TestEntity.class);

        //then
        verify(lowEntityBuilder).createEntity(same(component), any(TestEntity.class), same(localization), eq(TestEntity.class));
        verify(highEntityBuilder).createEntity(same(component), any(TestEntity.class), eq(localization), eq(TestEntity.class));
        assertSame(entity, result);
    }

    @Test
    public void shouldCallAllPageBuilders() throws ContentProviderException {
        //given
        Page page = mock(Page.class);
        ContentProvider contentProvider = mock(ContentProvider.class);
        PageModelImpl pageModel = new PageModelImpl();

        when(lowPageBuilder.createPage(same(page), any(PageModel.class), same(localization), same(contentProvider))).thenReturn(pageModel);
        when(highPageBuilder.createPage(same(page), any(PageModel.class), same(localization), same(contentProvider))).thenReturn(pageModel);

        //when
        PageModel result = modelBuilderPipeline.createPageModel(page, localization, contentProvider);

        //then
        verify(lowPageBuilder).createPage(same(page), any(PageModel.class), same(localization), same(contentProvider));
        verify(highPageBuilder).createPage(same(page), any(PageModel.class), same(localization), same(contentProvider));
        assertSame(pageModel, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSortPageAndEntityBuilders() {
        //when
        modelBuilderPipeline.init();

        //then
        List<PageBuilder> pageBuilderHandlers = (List<PageBuilder>) ReflectionTestUtils.getField(modelBuilderPipeline, "pageBuilderHandlers");
        List<EntityBuilder> entityBuilderHandlers = (List<EntityBuilder>) ReflectionTestUtils.getField(modelBuilderPipeline, "entityBuilderHandlers");

        assertThat(pageBuilderHandlers, IsIterableContainingInOrder.contains(highPageBuilder, lowPageBuilder));
        assertThat(entityBuilderHandlers, IsIterableContainingInOrder.contains(highEntityBuilder, lowEntityBuilder));
    }

    private static class TestEntity extends AbstractEntityModel {

    }

    @Configuration
    @Profile("test")
    public static class SpringConfigurationContext {

        @Bean
        public EntityBuilder lowEntityBuilder() {
            return getBuilder(EntityBuilder.class, Ordered.LOWEST_PRECEDENCE);
        }

        @Bean
        public EntityBuilder highEntityBuilder() {
            return getBuilder(EntityBuilder.class, Ordered.HIGHEST_PRECEDENCE);
        }

        @Bean
        public PageBuilder lowPageBuilder() {
            return getBuilder(PageBuilder.class, Ordered.LOWEST_PRECEDENCE);
        }

        @Bean
        public PageBuilder highPageBuilder() {
            return getBuilder(PageBuilder.class, Ordered.HIGHEST_PRECEDENCE);
        }

        @NotNull
        private <T extends Ordered> T getBuilder(Class<T> clazz, int order) {
            T mock = mock(clazz);
            when(mock.getOrder()).thenReturn(order);
            return mock;
        }

        @Bean
        public Localization localization() {
            return mock(Localization.class);
        }

        @Bean
        public ModelBuilderPipelineImpl modelBuilderPipeline() {
            return new ModelBuilderPipelineImpl();
        }
    }
}
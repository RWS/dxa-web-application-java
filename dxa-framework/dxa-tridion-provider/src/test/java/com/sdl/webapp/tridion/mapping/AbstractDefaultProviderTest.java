package com.sdl.webapp.tridion.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.DynamicList;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.query.ComponentMetadata;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.ImageUtils;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.factories.PageFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SuppressWarnings("ResultOfMethodCallIgnored")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class AbstractDefaultProviderTest {

    @Autowired
    private AbstractDefaultProvider abstractDefaultProvider;

    @Autowired
    private ModelBuilderPipeline modelBuilderPipeline;

    @Test
    public void shouldInjectIsQueryBasedParam() throws DxaException, ContentProviderException {
        //given
        Localization localization = mock(Localization.class);
        EntityModel entity = mock(EntityModel.class);
        when(entity.getXpmMetadata()).thenReturn(new HashMap<String, Object>());
        when(modelBuilderPipeline.createEntityModel(Matchers.<ComponentPresentation>any(), Matchers.<Localization>any()))
                .thenReturn(entity);

        //when
        EntityModel entityModel = abstractDefaultProvider.getEntityModel("1-1", localization);

        //then
        assertEquals(entityModel.getXpmMetadata().get("IsQueryBased"), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfIdIsWrong() throws DxaException {
        //when
        abstractDefaultProvider.getEntityModel("1", null);

        //then
        //exception is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfIdIsEmpty() throws DxaException {
        //when
        abstractDefaultProvider.getEntityModel("", null);

        //then
        //exception is thrown
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionIfIdIsNull() throws DxaException {
        //when
        abstractDefaultProvider.getEntityModel(null, null);

        //then
        //exception is thrown
    }

    @Test
    public void shouldCreateAllFoldersForNonExisting() throws ContentProviderException {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(false);

        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(true);

        //when
        //file.exists = false, parent.exists = true
        boolean notExist = AbstractDefaultProvider.isToBeRefreshed(file, 1000L);

        //then
        assertTrue(notExist);
    }

    @Test
    public void shouldSayFalseForFreshFile() throws ContentProviderException {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.lastModified()).thenReturn(1000L);
        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(false);
        when(parent.mkdirs()).thenReturn(true);

        //when
        //file.exists = false, parent.exists = false, parent.mkdirs = true
        boolean newFile = AbstractDefaultProvider.isToBeRefreshed(file, 500L);

        //then
        assertFalse(newFile);
    }

    @Test(expected = ContentProviderException.class)
    public void shouldFailForProblemsWithDirs() throws ContentProviderException {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(false);
        when(file.lastModified()).thenReturn(1000L);
        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(false);
        when(parent.mkdirs()).thenReturn(false);

        //when
        //file.exists = false, parent.exists = false, parent.mkdirs = false
        AbstractDefaultProvider.isToBeRefreshed(file, 500L);
        //then
        //exception
    }

    @Test
    public void shouldFindTheOldFileForRefreshFolderAction() throws ContentProviderException {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.lastModified()).thenReturn(1000L);
        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(true);

        //when
        //file.exists = true, parent.exists = true
        boolean oldFile = AbstractDefaultProvider.isToBeRefreshed(file, 1500L);

        //then
        verify(parent, never()).mkdirs();
        assertTrue(oldFile);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetQueryFromDynamicListAndCallExecuteImplementation() throws ContentProviderException {
        //given
        SimpleBrokerQuery query = new SimpleBrokerQuery();
        AbstractDefaultProvider spy = spy(abstractDefaultProvider);
        when(spy.executeQuery(eq(query))).thenReturn(new ArrayList<ComponentMetadata>() {{
            add(ComponentMetadata.builder().build());
            add(ComponentMetadata.builder().build());
        }});
        DynamicList dynamicList = mock(DynamicList.class);
        when(dynamicList.getQuery(any(Localization.class))).thenReturn(query);
        when(dynamicList.getEntity(any(ComponentMetadata.class))).thenReturn(new Link());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                List list = invocation.getArgumentAt(0, List.class);
                assertTrue(list.size() == 2);
                return true;
            }
        }).when(dynamicList).setQueryResults(anyList(), anyBoolean());


        //when
        spy.populateDynamicList(dynamicList, null);

        //then
        verify(dynamicList).getQuery(any(Localization.class));
        verify(spy).executeQuery(eq(query));
        verify(dynamicList, times(2)).getEntity(any(ComponentMetadata.class));
        verify(dynamicList).setQueryResults(anyList(), anyBoolean());
    }

    @Configuration
    static class SpringContext {

        @Bean
        public AbstractDefaultProvider defaultProvider() {
            return new AbstractDefaultProvider() {
                @Override
                protected StaticContentFile getStaticContentFile(File file, ImageUtils.StaticContentPathInfo pathInfo, int publicationId) throws ContentProviderException, IOException {
                    return mock(AbstractDefaultProvider.StaticContentFile.class);
                }

                @Override
                protected List<ComponentMetadata> executeQuery(SimpleBrokerQuery query) {
                    return Collections.emptyList();
                }
            };
        }

        @Bean
        public ComponentPresentationFactory componentPresentationFactory() {
            return mock(ComponentPresentationFactory.class);
        }

        @Bean
        public ModelBuilderPipeline modelBuilderPipeline() {
            return mock(ModelBuilderPipeline.class);
        }

        @Bean
        public PageFactory dd4tPageFactory() {
            return mock(PageFactory.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return mock(ObjectMapper.class);
        }

        @Bean
        public WebApplicationContext webApplicationContext() {
            return mock(WebApplicationContext.class);
        }

        @Bean
        public LinkResolver linkResolver() {
            return mock(LinkResolver.class);
        }

        @Bean
        public WebRequestContext webRequestContext() {
            return mock(WebRequestContext.class);
        }
    }
}
package com.sdl.webapp.tridion.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.ImageUtils;
import com.sdl.webapp.tridion.query.BrokerQuery;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.factories.PageFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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
        when(entity.getXpmMetadata()).thenReturn(new HashMap<String, String>());
        when(modelBuilderPipeline.createEntityModel(Matchers.<ComponentPresentation>any(), Matchers.<Localization>any()))
                .thenReturn(entity);

        //when
        EntityModel entityModel = abstractDefaultProvider.getEntityModel("1-1", localization);

        //then
        assertEquals(entityModel.getXpmMetadata().get("IsQueryBased"), String.valueOf(true));
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

    @Configuration
    static class SpringContext {
        @Bean
        public AbstractDefaultProvider defaultProvider() {
            return new AbstractDefaultProvider() {
                @Override
                protected StaticContentFile getStaticContentFile(File file, ImageUtils.StaticContentPathInfo pathInfo, int publicationId) throws ContentProviderException, IOException {
                    return mock(StaticContentFile.class);
                }

                @Override
                protected BrokerQuery instantiateBrokerQuery() {
                    return mock(BrokerQuery.class);
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
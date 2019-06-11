package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.dxa.tridion.content.CilStaticContentResolver;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.modelservice.DefaultModelServiceProvider;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.exceptions.DxaException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultContentProviderTest {

    @Mock
    private DefaultModelServiceProvider defaultModelService;

    @Mock
    private WebRequestContext webRequestContext;

    @Mock
    private ModelBuilderPipeline modelBuilderPipeline;

    @Mock
    private Localization localization;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private DefaultContentProvider contentProvider;

    @Before
    public void init() {
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(localization.getId()).thenReturn("42");
        when(webRequestContext.getLocalization()).thenReturn(localization);
        contentProvider = new DefaultContentProvider(webRequestContext, null, null, modelBuilderPipeline, defaultModelService, cacheManager);
    }

    @Test
    public void shouldBuildCorrectPageRequest() throws ContentProviderException {
        //given

        //when
        contentProvider.loadPage("/path", localization);

        //then
        verify(defaultModelService).loadPageModel(eq(PageRequestDto.builder(42, "/path").build()));
    }

    @Test
    public void shouldBuildCorrectEntityRequest() throws DxaException {
        //given
        when(modelBuilderPipeline.createEntityModel(any())).thenReturn(mock(EntityModel.class));

        //when
        contentProvider.getEntityModel("1-2");

        //then
        verify(defaultModelService).loadEntity(eq("42"), eq("1-2"));
    }

    @Test
    public void shouldFilterConditionalEntities() throws DxaException {
        //given
        PageModel pageModel = mock(PageModel.class);
        Localization localization = mock(Localization.class);
        when(localization.getId()).thenReturn("123");
        when(modelBuilderPipeline.createPageModel(any())).thenReturn(pageModel);
        when(pageModel.deepCopy()).thenReturn(pageModel);

        List<ConditionalEntityEvaluator> evaluators = Collections.emptyList();

        //when
        PageModel model = contentProvider.getPageModel("", localization);

        //then
        assertEquals(pageModel, model);
        verify(model).filterConditionalEntities(same(evaluators));
    }

    @Test
    public void shouldDelegateStaticContentResolver_ToStaticContentResolver() throws ContentProviderException {
        //given
        DefaultContentProvider provider = mock(DefaultContentProvider.class);
        when(provider.getStaticContent(anyString(), anyString(), anyString())).thenCallRealMethod();
        CilStaticContentResolver cilStaticContentResolver = mock(CilStaticContentResolver.class);
        WebRequestContext webRequestContext = mock(WebRequestContext.class);
        when(webRequestContext.getBaseUrl()).thenReturn("baseUrl");
        ReflectionTestUtils.setField(provider, "webRequestContext", webRequestContext);
        ReflectionTestUtils.setField(provider, "staticContentResolver", cilStaticContentResolver);

        //when
        provider.getStaticContent("path", "localizationId", "localizationPath");

        //then
        verify(cilStaticContentResolver).getStaticContent(eq(StaticContentRequestDto.builder("path", "localizationId")
                .localizationPath("localizationPath")
                .baseUrl("baseUrl")
                .build()));
    }
}
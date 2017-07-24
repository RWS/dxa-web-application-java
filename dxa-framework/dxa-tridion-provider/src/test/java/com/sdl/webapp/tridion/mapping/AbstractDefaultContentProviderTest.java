package com.sdl.webapp.tridion.mapping;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.dxa.tridion.content.StaticContentResolver;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.PageModel;
import org.junit.Test;
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

public class AbstractDefaultContentProviderTest {

    @Test
    public void shouldFilterConditionalEntities() throws ContentProviderException {
        //given 
        PageModel pageModel = mock(PageModel.class);

        List<ConditionalEntityEvaluator> evaluators = Collections.emptyList();

        AbstractDefaultContentProvider provider = mock(AbstractDefaultContentProvider.class);
        ReflectionTestUtils.setField(provider, "webRequestContext", mock(WebRequestContext.class));
        ReflectionTestUtils.setField(provider, "entityEvaluators", evaluators);
        when(provider.getPageModel(anyString(), any(Localization.class))).thenCallRealMethod();
        when(provider._loadPage(anyString(), any(Localization.class))).thenReturn(pageModel);

        //when
        PageModel model = provider.getPageModel("", null);

        //then
        assertEquals(pageModel, model);
        verify(model).filterConditionalEntities(same(evaluators));
    }

    @Test
    public void shouldDelegateStaticContentResolve_ToStaticContentResolver() throws ContentProviderException {
        //given 
        AbstractDefaultContentProvider provider = mock(AbstractDefaultContentProvider.class);
        when(provider.getStaticContent(anyString(), anyString(), anyString())).thenCallRealMethod();
        StaticContentResolver staticContentResolver = mock(StaticContentResolver.class);
        WebRequestContext webRequestContext = mock(WebRequestContext.class);
        when(webRequestContext.getBaseUrl()).thenReturn("baseUrl");
        ReflectionTestUtils.setField(provider, "webRequestContext", webRequestContext);
        ReflectionTestUtils.setField(provider, "staticContentResolver", staticContentResolver);

        //when
        provider.getStaticContent("path", "localizationId", "localizationPath");

        //then
        verify(staticContentResolver).getStaticContent(eq(StaticContentRequestDto.builder("path", "localizationId")
                .localizationPath("localizationPath")
                .baseUrl("baseUrl")
                .build()));
    }
}
package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.dxa.tridion.content.StaticContentResolver;
import com.sdl.dxa.tridion.graphql.GraphQLProvider;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.entity.Configuration;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GraphQLContentProviderTest {

    @Mock
    private Localization localization;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebRequestContext webRequestContext;
    @Mock
    private ModelBuilderPipeline builderPipeline;
    @Mock
    private StaticContentResolver staticContentResolver;
    @Mock
    private ApiClientProvider apiClientProvider;
    @Mock
    private GraphQLProvider graphQLProvider;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;

    @InjectMocks
    private GraphQLContentProvider contentProvider;

    @BeforeEach
    public void setup() {
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        contentProvider = spy(new GraphQLContentProvider(
                webRequestContext,
                staticContentResolver,
                builderPipeline,
                graphQLProvider,
                apiClientProvider,
                cacheManager
        ));
    }

    @Test
    public void getEntityModel() throws Exception {
        when(webRequestContext.getLocalization().getId()).thenReturn("5");
        Configuration actual = new Configuration();
        actual.setId("42");
        when(builderPipeline.createEntityModel(any())).thenReturn(actual);

        EntityModel result = contentProvider.getEntityModel("222-333", localization);

        assertEquals("42", result.getId());
    }

    @Test
    public void getPageModel() throws Exception {
        when(localization.getId()).thenReturn("5");
        PageModel actual = new DefaultPageModel();
        actual.setId("42");
        actual.setUrl("/path.html");
        when(builderPipeline.createPageModel(any())).thenReturn(actual);

        PageModel result = contentProvider.getPageModel("/path.html", localization);

        assertEquals("42", result.getId());
        assertEquals("/path.html", result.getUrl());
    }

    @Test
    public void getStaticContent() throws Exception {
        File contentFile = new File("path");
        when(staticContentResolver.getStaticContent(any(StaticContentRequestDto.class))).thenReturn(new StaticContentItem("testType",
                contentFile, false));

        StaticContentItem result = contentProvider.getStaticContent("/static", "localizationId", "localilzationPath");

        assertEquals("path", contentFile.getName());
        assertEquals("testType", result.getContentType());
    }
}
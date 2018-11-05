package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.dxa.modelservice.service.ModelServiceProvider;
import com.sdl.dxa.tridion.content.StaticContentResolver;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.entity.Configuration;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphQLContentProviderTest {

    @Mock
    private Localization localization;
    @Mock
    private ModelServiceProvider modelServiceProvider;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebRequestContext webRequestContext;
    @Mock
    private ModelBuilderPipeline builderPipeline;
    @Mock
    private StaticContentResolver staticContentResolver;

    @InjectMocks
    GraphQLContentProvider contentProvider;

    @Before
    public void setup() {

    }

    @Test
    public void getEntityModel() throws Exception {
        when(webRequestContext.getLocalization().getId()).thenReturn("5");
        when(modelServiceProvider.loadEntity("5", "222-333")).thenReturn(new EntityModelData());
        Configuration actual = new Configuration();
        actual.setId("42");
        when(builderPipeline.createEntityModel(any(EntityModelData.class))).thenReturn(actual);

        EntityModel result = contentProvider.getEntityModel("222-333", localization);

        assertEquals("42", result.getId());
    }

    @Test
    public void getPageModel() throws Exception {
        when(localization.getId()).thenReturn("5");
        when(modelServiceProvider.loadPageModel(any(PageRequestDto.class))).thenReturn(new PageModelData());
        PageModel actual = new DefaultPageModel();
        actual.setId("42");
        actual.setUrl("/path.html");
        when(builderPipeline.createPageModel(any(PageModelData.class))).thenReturn(actual);

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
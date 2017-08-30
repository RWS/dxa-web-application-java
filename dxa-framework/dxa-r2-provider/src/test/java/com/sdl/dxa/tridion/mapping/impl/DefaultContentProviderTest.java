package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.modelservice.DefaultModelService;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.exceptions.DxaException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultContentProviderTest {

    @Mock
    private DefaultModelService defaultModelService;

    @Mock
    private WebRequestContext webRequestContext;

    @Mock
    private ModelBuilderPipeline modelBuilderPipeline;

    @Mock
    private Localization localization;

    @InjectMocks
    private DefaultContentProvider contentProvider;

    @Before
    public void init() throws DxaException {
        when(localization.getId()).thenReturn("42");
        when(webRequestContext.getLocalization()).thenReturn(localization);
        EntityModel mock = mock(EntityModel.class);
        when(modelBuilderPipeline.createEntityModel(any(EntityModelData.class))).thenReturn(mock);
    }

    @Test
    public void shouldBuildCorrectPageRequest() throws ContentProviderException {
        //given

        //when
        contentProvider._loadPage("/path", localization);

        //then
        verify(defaultModelService).loadPageModel(eq(PageRequestDto.builder(42, "/path").build()));
    }

    @Test
    public void shouldBuildCorrectEntityRequest() throws ContentProviderException {
        //given

        //when
        contentProvider._getEntityModel("1-2");

        //then
        verify(defaultModelService).loadEntity(eq("42"), eq("1-2"));
    }
}
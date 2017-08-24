package com.sdl.dxa.tridion.navigation;

import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.tridion.modelservice.DefaultModelService;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StaticNavigationProviderTest {

    @Mock
    private DefaultModelService defaultModelService;

    @Mock
    private Localization localization;

    @InjectMocks
    private StaticNavigationProvider staticNavigationProvider;

    @Before
    public void init() throws ContentProviderException {
        when(localization.getId()).thenReturn("42");
        when(defaultModelService.loadPageContent(any(PageRequestDto.class))).thenReturn("");
    }

    @Test
    public void shouldBuildCorrectPageRequest() throws ContentProviderException {
        //given 

        //when
        staticNavigationProvider.getPageContent("/path", localization);

        //then
        verify(defaultModelService).loadPageContent(eq(PageRequestDto.builder(localization.getId(), "/path").build()));
    }
}
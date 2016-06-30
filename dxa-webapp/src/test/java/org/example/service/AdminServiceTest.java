package org.example.service;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdminServiceTest {

    @Mock
    private WebRequestContext webRequestContext;

    @Mock
    private LocalizationResolver localizationResolver;

    @InjectMocks
    private AdminService adminService;

    @Test
    public void shouldRefreshLocalization() throws Exception {
        //given
        String expected = "/index";
        Localization localization = mock(Localization.class);
        when(localization.getPath()).thenReturn(expected);
        when(webRequestContext.getLocalization()).thenReturn(localization);

        //when
        String result = adminService.refreshLocalization();

        //then
        verify(webRequestContext).getLocalization();
        verify(localizationResolver).refreshLocalization(same(localization));
        assertEquals("Should return expected path", expected, result);
    }

}
package com.sdl.webapp.common.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Splitter;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.navigation.NavigationProvider;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PageControllerTest {

    @Mock
    private NavigationProvider navigationProvider;

    @Mock
    private WebRequestContext webRequestContext;

    @InjectMocks
    private PageController pageController;

    @Test
    public void shouldSetIgnoreForXpmForNavigationJson() throws JsonProcessingException, NavigationProviderException {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        pageController.handleGetNavigationJson(request);

        //then
        assertNotNull(request.getAttribute("Ignore_By_Name_In_Request_Filter"));
        List<String> props = Splitter.on(",").splitToList(request.getAttribute("Ignore_By_Name_In_Request_Filter").toString());
        assertTrue(props.contains("XpmMetadata"));
        assertTrue(props.contains("XpmPropertyMetadata"));
    }
}
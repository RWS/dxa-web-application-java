package com.sdl.webapp.common.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Splitter;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.navigation.NavigationProvider;
import com.sdl.webapp.common.api.navigation.NavigationProviderException;
import com.sdl.webapp.common.exceptions.DxaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PageControllerTest {

    @Mock
    private NavigationProvider navigationProvider;

    @Mock
    private WebRequestContext webRequestContext;

    @Mock
    private LinkResolver linkResolver;

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

    @Test
    public void shouldConvertPageId_IntoTcmUri_WhenResolvingChildPublication() throws DxaException {
        //given
        lenient().when(linkResolver.resolveLink(eq("tcm:2-1-64"), eq("2"))).thenReturn("resolvedUrl");

        //when
        String redirect = pageController.handleResolve("1", "2", "", "");

        //then
        assertEquals("redirect:resolvedUrl", redirect);
    }

    @Test
    public void shouldFallback_IfChildPublication_CannotBeResolved() throws DxaException {
        //given 

        //when
        String redirect = pageController.handleResolve("1", "2", "defaultPath", "defaultItem");
        String redirect2 = pageController.handleResolve("1", "2", "", "defaultItem");

        //then
        assertEquals("redirect:defaultPath", redirect);
        assertEquals("redirect:/", redirect2);
    }

    @Test
    public void shouldResolveDefaultItem_IfPublication_IsNotResolvable() throws DxaException {
        //given 
        lenient().when(linkResolver.resolveLink(eq("tcm:2-45-56"), eq("2"))).thenReturn("resolvedDefaultUrl");

        //when
        String redirect = pageController.handleResolve("1", "2", "defaultPath", "tcm:2-45-56");
        String redirect2 = pageController.handleResolve("1", "2", "defaultPath", "");
        String redirect3 = pageController.handleResolve("1", "2", "", "");

        //then
        assertEquals("redirect:resolvedDefaultUrl", redirect);
        assertEquals("redirect:defaultPath", redirect2);
        assertEquals("redirect:/", redirect3);
    }
}
package com.sdl.webapp.common.controller.api;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.api.navigation.OnDemandNavigationProvider;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OnDemandNavigationControllerTest {

    @Mock
    private OnDemandNavigationProvider onDemandNavigationProvider;

    @Mock
    private WebRequestContext webRequestContext;

    @InjectMocks
    private OnDemandNavigationController controller;

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionInCaseOnDemandIsNotEnabled() throws DxaItemNotFoundException {
        //given
        OnDemandNavigationController controller = new OnDemandNavigationController(null, null);

        //when
        controller.handle("", true, 0, new MockHttpServletRequest());

        //then
        //UOE
    }

    @Test
    public void shouldCreateNavigationFilterAndPassItToNavigationProvider() throws DxaItemNotFoundException {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletRequest request2 = new MockHttpServletRequest();

        //when
        controller.handle("t1-k23", true, 123, request);
        controller.handle(true, 123, request2);

        //then
        try {
            verify(onDemandNavigationProvider).getNavigationSubtree(eq("t1-k23"), argThat(new BaseMatcher<NavigationFilter>() {
                @Override
                public boolean matches(Object item) {
                    NavigationFilter filter = (NavigationFilter) item;
                    return filter.isWithAncestors() && filter.getDescendantLevels() == 123;
                }

                @Override
                public void describeTo(Description description) {

                }
            }), any(Localization.class));
        } catch (com.sdl.webapp.common.exceptions.DxaItemNotFoundException e) {
            e.printStackTrace();
        }
        List<String> properties = Arrays.asList(request.getAttribute("Ignore_By_Name_In_Request_Filter").toString().split(","));
        List<String> properties2 = Arrays.asList(request2.getAttribute("Ignore_By_Name_In_Request_Filter").toString().split(","));
        assertTrue(properties.contains("XpmMetadata"));
        assertTrue(properties2.contains("XpmMetadata"));
        assertTrue(properties.contains("XpmPropertyMetadata"));
        assertTrue(properties2.contains("XpmPropertyMetadata"));
    }
}
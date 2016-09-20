package com.sdl.webapp.common.controller.api;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.api.navigation.OnDemandNavigationProvider;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    public void shouldThrowExceptionInCaseOnDemandIsNotEnabled() {
        //given
        OnDemandNavigationController controller = new OnDemandNavigationController(null, null);

        //when
        controller.handle("", true, 0);

        //then
        //UOE
    }

    @Test
    public void shouldCreateNavigationFilterAndPassItToNavigationProvider() {
        //given

        //when
        controller.handle("t1-k23", true, 123);

        //then
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
    }
}
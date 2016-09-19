package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.api.navigation.OnDemandNavigationProvider;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
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
public class ApiControllerTest {

    @Mock
    private OnDemandNavigationProvider onDemandNavigationProvider;

    @Mock
    private WebRequestContext webRequestContext;

    @InjectMocks
    private ApiController apiController;

    @Before
    public void before() {

    }


    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionInCaseOnDemandIsNotEnabled() {
        //given
        ApiController controller = new ApiController();

        //when
        controller.handleGetNavigationSubtree("", true, 0);

        //then
        //UOE
    }

    @Test
    public void shouldCreateNavigationFilterAndPassItToNavigationProvider() {
        //given

        //when
        apiController.handleGetNavigationSubtree("t1-k23", true, 123);

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
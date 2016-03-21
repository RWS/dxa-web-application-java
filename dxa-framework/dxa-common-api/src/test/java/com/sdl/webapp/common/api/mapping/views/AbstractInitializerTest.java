package com.sdl.webapp.common.api.mapping.views;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.Article;
import com.sdl.webapp.common.api.model.entity.Image;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.Location;
import com.sdl.webapp.common.api.model.entity.Place;
import com.sdl.webapp.common.api.model.entity.YouTubeVideo;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AbstractInitializerTest {

    private static ArgumentMatcher<MvcData> getViewNameMatcher(final String viewName) {
        return new ArgumentMatcher<MvcData>() {
            @Override
            public boolean matches(Object argument) {
                return ((MvcData) argument).getViewName().equals(viewName);
            }
        };
    }

    @Test
    public void shouldRegisterAllKnownAnnotations() {
        //given
        AbstractInitializer initializer = new TestClass();
        ViewModelRegistry registry = mock(ViewModelRegistry.class);
        ReflectionTestUtils.setField(initializer, "viewModelRegistry", registry);

        //when
        ReflectionTestUtils.invokeMethod(initializer, "initialize");

        //then
        verify(registry).registerViewModel(Matchers.argThat(getViewNameMatcher("test1")), eq(Article.class));
        verify(registry).registerViewModel(Matchers.argThat(getViewNameMatcher("test2")), eq(Image.class));
        verify(registry).registerViewModel(Matchers.argThat(getViewNameMatcher("test2.1")), eq(Place.class));
        verify(registry).registerViewModel(Matchers.argThat(getViewNameMatcher("test3")), eq(Location.class));
        verify(registry).registerViewModel(Matchers.argThat(getViewNameMatcher("test4")), eq(Link.class));
        verify(registry).registerViewModel(Matchers.argThat(getViewNameMatcher("test4.1")), eq(YouTubeVideo.class));
    }

    @RegisteredView(viewName = "test1", clazz = Article.class)
    @RegisteredViews({
            @RegisteredView(viewName = "test2", clazz = Image.class),
            @RegisteredView(viewName = "test2.1", clazz = Place.class)
    })
    @RegisteredViewModel(viewName = "test3", modelClass = Location.class)
    @RegisteredViewModels({
            @RegisteredViewModel(viewName = "test4", modelClass = Link.class),
            @RegisteredViewModel(viewName = "test4.1", modelClass = YouTubeVideo.class)
    })
    private static class TestClass extends AbstractInitializer {
        @Override
        protected String getAreaName() {
            return "area";
        }
    }

}
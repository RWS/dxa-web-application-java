package com.sdl.webapp.common.api.mapping.views;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Objects;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AbstractInitializerTest {

    private static ArgumentMatcher<MvcData> getViewNameMatcher(final String viewName) {
        return new ArgumentMatcher<MvcData>() {
            @Override
            public boolean matches(Object argument) {
                return argument == null ? isNullOrEmpty(viewName) :
                        Objects.equals(((MvcData) argument).getViewName(), viewName);
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
        verify(registry).registerViewModel(Matchers.argThat(getViewNameMatcher("test1")), eq(Test1.class));
        verify(registry).registerViewModel(Matchers.argThat(getViewNameMatcher("test2")), eq(Test2.class));
        verify(registry).registerViewModel(Matchers.argThat(getViewNameMatcher("test2.1")), eq(Test21.class));
        verify(registry).registerViewModel(Matchers.argThat(getViewNameMatcher("test3")), eq(Test3.class));
        verify(registry).registerViewModel(Matchers.argThat(getViewNameMatcher("test4")), eq(Test4.class));
        verify(registry).registerViewModel(Matchers.argThat(getViewNameMatcher("test4.1")), eq(Test41.class));
    }

    @RegisteredView(viewName = "test1", clazz = Test1.class)
    @RegisteredViews({
            @RegisteredView(viewName = "test2", clazz = Test2.class),
            @RegisteredView(viewName = "test2.1", clazz = Test21.class),
            @RegisteredView(clazz = NoView1.class)
    })
    @RegisteredViewModel(viewName = "test3", modelClass = Test3.class)
    @RegisteredViewModels({
            @RegisteredViewModel(viewName = "test4", modelClass = Test4.class),
            @RegisteredViewModel(viewName = "test4.1", modelClass = Test41.class),
            @RegisteredViewModel(modelClass = NoView2.class)
    })
    private static class TestClass extends AbstractInitializer {

        @Override
        protected String getAreaName() {
            return "area";
        }
    }

    private static class Test1 extends AbstractEntityModel {

    }

    private static class Test2 extends AbstractEntityModel {

    }

    private static class Test21 extends AbstractEntityModel {

    }

    private static class Test3 extends AbstractEntityModel {

    }

    private static class Test4 extends AbstractEntityModel {

    }

    private static class Test41 extends AbstractEntityModel {

    }

    private static class NoView1 extends AbstractEntityModel {

    }

    private static class NoView2 extends AbstractEntityModel {

    }

}
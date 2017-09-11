package com.sdl.webapp.common.api.mapping.views;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.AbstractViewModel;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AbstractInitializerTest {

    private static final String AREA = "area";

    @Mock
    private ViewModelRegistry registry;

    @InjectMocks
    private AbstractModuleInitializer initializer = new TestClass();

    @Test
    public void shouldRegisterAllKnownAnnotations() {
        //when
        ReflectionTestUtils.invokeMethod(initializer, "initialize");

        //then
        verifyRegistration("test3", "Entity", Test3.class);

        verifyRegistration("test4", "Entity", Test4.class);
        verifyRegistration("test4.1", "Entity", Test41.class);
        verifyRegistration(null, "Entity", NoView2.class);
        verifyRegistration(null, "Page", NoViewPage.class);
        verifyRegistration(null, "Region", NoViewRegion.class);
        verifyRegistration(null, null, NotEntity.class);
        verifyRegistration(null, "Hello", NotEntity.class);
    }

    private void verifyRegistration(final String viewName, final String controllerName, Class<? extends ViewModel> clazz) {
        verify(registry).registerViewModel(argThat(new ArgumentMatcher<MvcData>() {
            @Override
            public boolean matches(Object argument) {
                if (argument == null || viewName == null) {
                    return viewName == null && argument == null;
                }

                MvcData mvcData = (MvcData) argument;

                if (EntityModel.class.isAssignableFrom(clazz) ||
                        PageModel.class.isAssignableFrom(clazz) ||
                        RegionModel.class.isAssignableFrom(clazz)) {
                    assertNotNull(mvcData.getControllerName());
                }

                return mvcData.getViewName().equals(viewName)
                        && mvcData.getControllerName().equals(controllerName)
                        && mvcData.getAreaName().equals(AREA);
            }
        }), eq(clazz));
    }

    @RegisteredViewModel(viewName = "test3", modelClass = Test3.class)
    @RegisteredViewModels({
            @RegisteredViewModel(viewName = "test4", modelClass = Test4.class),
            @RegisteredViewModel(viewName = "test4.1", modelClass = Test41.class),
            @RegisteredViewModel(modelClass = NoView2.class),
            @RegisteredViewModel(modelClass = NoViewPage.class),
            @RegisteredViewModel(modelClass = NoViewRegion.class),
            @RegisteredViewModel(modelClass = NotEntity.class),
            @RegisteredViewModel(modelClass = NotEntity2.class, controllerName = "Hello")
    })
    private static class TestClass extends AbstractModuleInitializer {

        @Override
        protected String getAreaName() {
            return AREA;
        }
    }

    private static class Test3 extends AbstractEntityModel {

    }

    private static class Test4 extends AbstractEntityModel {

    }

    private static class Test41 extends AbstractEntityModel {

    }

    private static class NoView2 extends AbstractEntityModel {

    }

    private static class NoViewPage extends DefaultPageModel {

    }

    private static class NoViewRegion extends RegionModelImpl {

        public NoViewRegion(String name) throws DxaException {
            super(name);
        }
    }

    private static class NotEntity extends AbstractViewModel {

        @Override
        public String getXpmMarkup(Localization localization) {
            return null;
        }
    }

    private static class NotEntity2 extends AbstractViewModel {

        @Override
        public String getXpmMarkup(Localization localization) {
            return null;
        }
    }

}
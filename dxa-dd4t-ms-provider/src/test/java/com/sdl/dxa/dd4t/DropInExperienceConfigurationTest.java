package com.sdl.dxa.dd4t;

import com.sdl.dxa.dd4t.providers.ModelServiceComponentPresentationProvider;
import com.sdl.dxa.dd4t.providers.ModelServicePageProvider;
import org.dd4t.core.factories.impl.ComponentPresentationFactoryImpl;
import org.dd4t.core.factories.impl.PageFactoryImpl;
import org.dd4t.providers.ComponentPresentationProvider;
import org.dd4t.providers.PageProvider;
import org.dd4t.providers.PayloadCacheProvider;
import org.dd4t.providers.impl.BrokerComponentPresentationProvider;
import org.dd4t.providers.impl.BrokerPageProvider;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DropInExperienceConfigurationTest {

    @Test
    public void shouldReplacePageProvider_IfEverythingIsSet() {
        //given 
        PageFactoryImpl pageFactory = mock(PageFactoryImpl.class);
        ComponentPresentationFactoryImpl componentPresentationFactoryImpl = mock(ComponentPresentationFactoryImpl.class);
        DropInExperienceConfiguration configuration = new DropInExperienceConfiguration(
                pageFactory, componentPresentationFactoryImpl,
                new BrokerPageProvider(), new BrokerComponentPresentationProvider(),
                mock(PayloadCacheProvider.class));

        //when
        configuration.init();

        //then
        verify(pageFactory).setPageProvider(argThat(new BaseMatcher<PageProvider>() {
            @Override
            public boolean matches(Object o) {
                assertTrue(o instanceof ModelServicePageProvider);
                assertFalse(((ModelServicePageProvider) o).isContentIsBase64Encoded());
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Page Provider is replaced");
            }
        }));

        verify(componentPresentationFactoryImpl).setComponentPresentationProvider(argThat(new BaseMatcher<ComponentPresentationProvider>() {
            @Override
            public boolean matches(Object o) {
                assertTrue(o instanceof ModelServiceComponentPresentationProvider);
                assertFalse(((ModelServiceComponentPresentationProvider) o).isContentIsBase64Encoded());
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("ComponentPresentationProvider is replaced");
            }
        }));
    }
}
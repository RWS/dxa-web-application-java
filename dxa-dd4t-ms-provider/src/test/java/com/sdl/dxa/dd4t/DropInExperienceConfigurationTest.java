package com.sdl.dxa.dd4t;

import com.sdl.dxa.dd4t.providers.ModelServiceComponentPresentationProvider;
import com.sdl.dxa.dd4t.providers.ModelServicePageProvider;
import org.dd4t.core.factories.impl.ComponentPresentationFactoryImpl;
import org.dd4t.core.factories.impl.PageFactoryImpl;
import org.dd4t.providers.ComponentPresentationProvider;
import org.dd4t.providers.PageProvider;
import org.dd4t.providers.PayloadCacheProvider;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DropInExperienceConfigurationTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private PageFactoryImpl pageFactory;

    @Mock
    private ComponentPresentationFactoryImpl componentPresentationFactoryImpl;

    @Mock
    private PayloadCacheProvider payloadCacheProvider;

    @InjectMocks
    private DropInExperienceConfiguration configuration;

    @Before
    public void init() {
        when(applicationContext.getBean(eq(PageFactoryImpl.class))).thenReturn(pageFactory);
        when(applicationContext.getBean(eq(ComponentPresentationFactoryImpl.class))).thenReturn(componentPresentationFactoryImpl);
        when(applicationContext.getBean(eq(PayloadCacheProvider.class))).thenReturn(payloadCacheProvider);
    }

    @Test
    public void shouldReplacePageProvider_IfEverythingIsSet() {
        //given

        //when
        configuration.setApplicationContext(applicationContext);

        //then
        verify(pageFactory).setPageProvider(argThat(new BaseMatcher<PageProvider>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof ModelServicePageProvider;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Page Provider is replaced");
            }
        }));

        verify(componentPresentationFactoryImpl).setComponentPresentationProvider(argThat(new BaseMatcher<ComponentPresentationProvider>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof ModelServiceComponentPresentationProvider;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("ComponentPresentationProvider is replaced");
            }
        }));
    }
}
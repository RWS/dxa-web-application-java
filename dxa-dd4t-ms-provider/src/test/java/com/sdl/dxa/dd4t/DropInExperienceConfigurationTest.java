package com.sdl.dxa.dd4t;

import com.sdl.dxa.dd4t.providers.ModelServiceComponentPresentationProvider;
import com.sdl.dxa.dd4t.providers.ModelServicePageProvider;
import org.dd4t.core.factories.impl.ComponentPresentationFactoryImpl;
import org.dd4t.core.factories.impl.PageFactoryImpl;
import org.dd4t.providers.PayloadCacheProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    public void init() {
        lenient().when(applicationContext.getBean(eq(PageFactoryImpl.class))).thenReturn(pageFactory);
        lenient().when(applicationContext.getBean(eq(ComponentPresentationFactoryImpl.class))).thenReturn(componentPresentationFactoryImpl);
        lenient().when(applicationContext.getBean(eq(PayloadCacheProvider.class))).thenReturn(payloadCacheProvider);
    }

    @Test
    public void shouldReplacePageProvider_IfEverythingIsSet() {
        //given

        //when
        configuration.setApplicationContext(applicationContext);

        //then
        verify(pageFactory).setPageProvider(argThat(new ArgumentMatcher<ModelServicePageProvider>() {
            @Override
            public boolean matches(ModelServicePageProvider pageProvider) {
                return true;
            }
            @Override
            public Class<?> type() {
                return ArgumentMatcher.super.type();
            }
        }));

        verify(componentPresentationFactoryImpl).setComponentPresentationProvider(argThat(new ArgumentMatcher<ModelServiceComponentPresentationProvider>() {
            @Override
            public boolean matches(ModelServiceComponentPresentationProvider modelServiceComponentPresentationProvider) {
                return true;
            }
            @Override
            public Class<?> type() {
                return ArgumentMatcher.super.type();
            }
        }));
    }
}
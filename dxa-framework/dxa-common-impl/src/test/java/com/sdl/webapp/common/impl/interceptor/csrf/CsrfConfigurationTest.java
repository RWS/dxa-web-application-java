package com.sdl.webapp.common.impl.interceptor.csrf;

import org.junit.Test;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class CsrfConfigurationTest {

    @Test
    public void shouldDisableCsrfProtectionIfPropertyIsSet() {
        //given 
        InterceptorRegistry registry = mock(InterceptorRegistry.class);

        //when
        new CsrfConfiguration().addInterceptors(registry);

        //then
        verifyZeroInteractions(registry);
    }
}
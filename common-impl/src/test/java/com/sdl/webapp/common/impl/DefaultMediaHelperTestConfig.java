package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.WebRequestContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for {@code DefaultMediaHelperTest}.
 */
@Configuration
public class DefaultMediaHelperTestConfig {

    @Bean
    public GenericMediaHelper mediaHelper() {
        return new GenericMediaHelper(webRequestContext());
    }

    @Bean
    public WebRequestContext webRequestContext() {
        return new WebRequestContextImpl() {
            @Override
            public int getDisplayWidth() {
                return 1920;
            }

            @Override
            public double getPixelRatio() {
                return 1.0;
            }

            @Override
            public int getMaxMediaWidth() {
                return 2048;
            }
        };
    }
}

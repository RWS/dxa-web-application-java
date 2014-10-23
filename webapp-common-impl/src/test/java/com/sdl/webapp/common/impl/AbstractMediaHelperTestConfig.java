package com.sdl.webapp.common.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for {@code AbstractMediaHelperTest}.
 */
@Configuration
public class AbstractMediaHelperTestConfig {

    @Bean
    public AbstractMediaHelper mediaHelper() {
        return new AbstractMediaHelper() {
            @Override
            public String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize) {
                return url;
            }
        };
    }

    @Bean
    public MockWebRequestContext webRequestContext() {
        return new MockWebRequestContext();
    }
}

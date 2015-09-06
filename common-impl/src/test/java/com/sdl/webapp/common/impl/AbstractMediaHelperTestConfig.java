package com.sdl.webapp.common.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Spring configuration for {@code AbstractMediaHelperTest}.
 */
@Configuration
public class AbstractMediaHelperTestConfig {

    @Bean
    @Lazy
    public AbstractMediaHelper mediaHelper() {
        return new MockMediaHelper(webRequestContext());
    }

    @Bean
    public MockWebRequestContext webRequestContext() {
        return new MockWebRequestContext();
    }
}

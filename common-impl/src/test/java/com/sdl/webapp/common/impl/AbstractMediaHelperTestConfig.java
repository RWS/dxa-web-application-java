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
        return new MockMediaHelper(webRequestContext());
    }

    @Bean
    public MockWebRequestContext webRequestContext() {
        return new MockWebRequestContext();
    }
}

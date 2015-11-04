package com.sdl.webapp.common.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Spring configuration for {@link DefaultMediaHelperTest}.
 */
@Configuration
public class AbstractMediaHelperTestConfig {

    @Bean
    public DefaultMediaHelper mediaHelper() {
        return new DefaultMediaHelper();
    }

    @Bean
    public MockWebRequestContext webRequestContext() {
        return new MockWebRequestContext();
    }

    @Bean
    public MockContextEngine contextEngine() {
        return new MockContextEngine(new MockContextClaimsProvider());
    }
}

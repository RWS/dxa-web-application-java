package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.MediaHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for {@link DefaultMediaHelperTest}.
 */
@Configuration
public class AbstractMediaHelperTestConfig {

    @Bean
    public MediaHelper mediaHelper() {
        return new DefaultMediaHelper();
    }

    @Bean
    //todo refactor to use normal mocks
    public MockWebRequestContext webRequestContext() {
        return new MockWebRequestContext();
    }

    @Bean
    public MockContextEngine contextEngine() {
        return new MockContextEngine(new MockContextClaimsProvider());
    }
}

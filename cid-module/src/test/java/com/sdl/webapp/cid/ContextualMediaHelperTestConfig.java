package com.sdl.webapp.cid;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.impl.WebRequestContextImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Spring configuration for {@code ContextualMediaHelperTest}.
 */
@Configuration
public class ContextualMediaHelperTestConfig {

    @Bean
    @Lazy
    public ContextualMediaHelper mediaHelper() {
        return new ContextualMediaHelper(webRequestContext());
    }
    
    @Bean
    public MockContextEngine contextEngine() {
        return new MockContextEngine(new MockContextClaimsProvider());
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

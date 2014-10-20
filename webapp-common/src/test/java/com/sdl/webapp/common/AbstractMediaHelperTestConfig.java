package com.sdl.webapp.common;

import com.sdl.webapp.common.impl.AbstractMediaHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public TestWebRequestContext webRequestContext() {
        return new TestWebRequestContext();
    }
}

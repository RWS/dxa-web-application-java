package com.sdl.tridion.referenceimpl.cid;

import com.sdl.tridion.referenceimpl.common.MediaHelperProvider;
import com.sdl.tridion.referenceimpl.common.config.WebRequestContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContextualMediaHelperTestConfig {

    @Bean
    public MediaHelperProvider mediaHelperProvider() {
        return new MediaHelperProvider();
    }

    @Bean
    public ContextualMediaHelper mediaHelper() {
        return new ContextualMediaHelper();
    }

    @Bean
    public WebRequestContext webRequestContext() {
        return new WebRequestContext() {
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

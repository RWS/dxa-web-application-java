package com.sdl.tridion.referenceimpl.dd4t;

import com.sdl.tridion.referenceimpl.common.MediaHelperProvider;
import com.sdl.tridion.referenceimpl.common.config.WebRequestContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DD4TMediaHelperTestConfig {

    @Bean
    public MediaHelperProvider mediaHelperProvider() {
        return new MediaHelperProvider();
    }

    @Bean
    public DD4TMediaHelper mediaHelper() {
        return new DD4TMediaHelper();
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

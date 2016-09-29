package com.sdl.webapp.common.impl.interceptor.csrf;

import com.sdl.webapp.common.util.InitializationUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class CsrfConfiguration extends WebMvcConfigurerAdapter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (!InitializationUtils.loadDxaProperties().containsKey("dxa.csrf.allowed")) {
            registry.addInterceptor(csrfInterceptor());
        }
    }

    @Bean
    public CsrfInterceptor csrfInterceptor() {
        return new CsrfInterceptor();
    }
}

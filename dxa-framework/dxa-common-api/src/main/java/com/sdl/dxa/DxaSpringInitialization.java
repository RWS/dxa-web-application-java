package com.sdl.dxa;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

import static com.sdl.webapp.common.util.InitializationUtils.traceBeanInitialization;

/**
 * <p>Entry point for Spring initialization for DXA Framework which also triggers initialization for default paths
 * for parts of DXA and modules.</p>
 * <p>If imported to a custom Spring context, fully initializes DXA.</p>
 */
@Configuration
//todo dxa2 rename com.sdl.webapp to com.sdl.dxa
@ComponentScan(basePackages = {"com.sdl.webapp", "com.sdl.dxa"})
//TODO dxa2 remove resource auto-import
@ImportResource("classpath*:/META-INF/spring-context.xml")
@Slf4j
public class DxaSpringInitialization {

    @Bean
    @SneakyThrows(IOException.class)
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        patternResolver.setPathMatcher(new AntPathMatcher());
        //todo dxa2 what is the order of loaded properties?
        configurer.setLocations(patternResolver.getResources("classpath*:/dxa.**.properties"));
        traceBeanInitialization(configurer);
        return configurer;
    }
}

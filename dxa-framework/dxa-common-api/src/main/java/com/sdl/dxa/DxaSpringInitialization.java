package com.sdl.dxa;

import com.sdl.webapp.common.util.InitializationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;

import java.util.Collection;

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
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

        Collection<Resource> resources = InitializationUtils.getAllResources();
        configurer.setLocations(resources.toArray(new Resource[resources.size()]));

        traceBeanInitialization(configurer);

        return configurer;
    }
}

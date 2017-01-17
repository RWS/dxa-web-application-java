package com.sdl.dxa.compatible.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Compatible configuration for DXA. Make sure to import this configuration if you use compatible mode. If auto-scan is
 * enabled then this will be imported for you. By default auto-scan is enabled.
 * @since 2.0
 */
@Configuration
@ImportResource("classpath*:/META-INF/spring-context.xml")
public class SpringInitialization {
}

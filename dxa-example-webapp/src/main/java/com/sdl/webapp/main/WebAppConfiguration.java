package com.sdl.webapp.main;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.sdl.webapp", "com.sdl.dxa"})
public class WebAppConfiguration {
}
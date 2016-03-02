package com.sdl.webapp.main;

import com.sdl.dxa.DxaSpringInitialization;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.sdl.webapp")
@Import(DxaSpringInitialization.class)
public class WebAppConfiguration {
}
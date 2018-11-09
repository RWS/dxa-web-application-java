package com.sdl.webapp.config;

import com.sdl.web.api.content.BinaryContentRetriever;
import com.sdl.web.api.dynamic.BinaryContentRetrieverImpl;
import com.sdl.web.api.dynamic.DynamicMappingsRetriever;
import com.sdl.web.api.dynamic.DynamicMappingsRetrieverImpl;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@ComponentScan({"com.sdl.webapp.tridion", "com.sdl.dxa.tridion"})
@Configuration
public class TridionSpringConfiguration {

    @Bean
    @Profile("cil.providers.active")
    public DynamicMetaRetriever dynamicMetaRetriever() {
        return new DynamicMetaRetriever();
    }

    @Bean
    @Profile("cil.providers.active")
    public BinaryContentRetriever binaryContentRetriever() {
        return new BinaryContentRetrieverImpl();
    }

    @Bean
    @Profile("cil.providers.active")
    public DynamicMappingsRetriever dynamicMappingsRetriever() {
        return new DynamicMappingsRetrieverImpl();
    }
}

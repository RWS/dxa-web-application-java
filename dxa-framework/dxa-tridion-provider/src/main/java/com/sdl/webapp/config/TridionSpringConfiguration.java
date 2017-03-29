package com.sdl.webapp.config;

import com.sdl.web.api.content.BinaryContentRetriever;
import com.sdl.web.api.dynamic.BinaryContentRetrieverImpl;
import com.sdl.web.api.dynamic.DynamicMappingsRetriever;
import com.sdl.web.api.dynamic.DynamicMappingsRetrieverImpl;
import com.sdl.web.api.taxonomies.TaxonomyRelationManager;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.taxonomies.TaxonomyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan({"com.sdl.webapp.tridion", "com.sdl.dxa.tridion"})
@Configuration
public class TridionSpringConfiguration {

    @Bean
    public DynamicMetaRetriever dynamicMetaRetriever() {
        return new DynamicMetaRetriever();
    }

    @Bean
    public BinaryContentRetriever binaryContentRetriever() {
        return new BinaryContentRetrieverImpl();
    }

    @Bean
    public DynamicMappingsRetriever dynamicMappingsRetriever() {
        return new DynamicMappingsRetrieverImpl();
    }

    @Bean
    public TaxonomyFactory webTaxonomyFactory() {
        return new TaxonomyFactory();
    }

    @Bean
    public TaxonomyRelationManager taxonomyRelationManager() {
        return new TaxonomyRelationManager();
    }
}

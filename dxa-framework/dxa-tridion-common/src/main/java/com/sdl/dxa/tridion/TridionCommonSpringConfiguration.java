package com.sdl.dxa.tridion;

import com.sdl.web.api.content.BinaryContentRetriever;
import com.sdl.web.api.dynamic.BinaryContentRetrieverImpl;
import com.sdl.web.api.dynamic.taxonomies.WebTaxonomyFactory;
import com.sdl.web.api.meta.WebPublicationMetaFactory;
import com.sdl.web.api.meta.WebPublicationMetaFactoryImpl;
import com.sdl.web.api.taxonomies.WebTaxonomyFactoryImpl;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.taxonomies.TaxonomyRelationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@ComponentScan("com.sdl.dxa.tridion")
@Configuration
public class TridionCommonSpringConfiguration {

    @Bean
    @Profile("cil.providers.active")
    public WebTaxonomyFactory webTaxonomyFactory() {
        return new WebTaxonomyFactoryImpl();
    }

    @Bean
    @Profile("cil.providers.active")
    public TaxonomyRelationManager taxonomyRelationManager() {
        return new TaxonomyRelationManager();
    }

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
    public WebPublicationMetaFactory webPublicationMetaFactory() {
        return new WebPublicationMetaFactoryImpl();
    }

}

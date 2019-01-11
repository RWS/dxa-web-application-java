package com.sdl.dxa.tridion;

import com.sdl.web.api.dynamic.taxonomies.WebTaxonomyFactory;
import com.tridion.taxonomies.TaxonomyFactory;
import com.tridion.taxonomies.TaxonomyRelationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("com.sdl.dxa.tridion")
@Configuration
public class TridionCommonSpringConfiguration {

    @Bean
    public WebTaxonomyFactory webTaxonomyFactory() {
        return new TaxonomyFactory();
    }

    @Bean
    public TaxonomyRelationManager taxonomyRelationManager() {
        return new TaxonomyRelationManager();
    }
}

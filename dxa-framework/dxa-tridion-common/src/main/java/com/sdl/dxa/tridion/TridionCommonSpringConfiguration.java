package com.sdl.dxa.tridion;

import com.sdl.web.api.content.BinaryContentRetriever;
import com.sdl.web.api.dynamic.BinaryContentRetrieverImpl;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.meta.PublicationMetaFactory;
import com.tridion.taxonomies.TaxonomyFactory;
import com.tridion.taxonomies.TaxonomyRelationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Tridion common spring configuration.
 *
 * @deprecated since PCA implementation added which supports mashup scenario.
 */
@ComponentScan("com.sdl.dxa.tridion")
@Configuration
@Deprecated
public class TridionCommonSpringConfiguration {

    @Bean
    // TODO uncomment when tridion docs module will use graphql providers
    // @Profile("cil.providers.active")
    public TaxonomyFactory webTaxonomyFactory() {
        return new TaxonomyFactory();
    }

    @Bean
    // TODO uncomment when tridion docs module will use graphql providers
    // @Profile("cil.providers.active")
    public TaxonomyRelationManager taxonomyRelationManager() {
        return new TaxonomyRelationManager();
    }

    @Bean
    @Profile("cil.providers.active")
    public DynamicMetaRetriever dynamicMetaRetriever() {
        return new DynamicMetaRetriever();
    }

    @Bean
    // TODO uncomment when tridion docs module will use graphql providers
    // @Profile("cil.providers.active")
    public BinaryContentRetriever binaryContentRetriever() {
        return new BinaryContentRetrieverImpl();
    }

    @Bean
    @Profile("cil.providers.active")
    public PublicationMetaFactory webPublicationMetaFactory() {
        return new PublicationMetaFactory();
    }

}

package com.sdl.webapp.tridion;

import com.tridion.content.BinaryFactory;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.factories.impl.ComponentPresentationFactoryImpl;
import org.dd4t.core.providers.EHCacheProvider;
import org.dd4t.providers.ComponentPresentationProvider;
import org.dd4t.providers.impl.BrokerLinkProvider;
import org.dd4t.providers.impl.BrokerPageProvider;
import org.dd4t.providers.impl.BrokerTaxonomyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringContextConfiguration {

    @Autowired
    private EHCacheProvider ehCacheProvider;

    @Bean
    public BrokerLinkProvider linkProvider() {
        BrokerLinkProvider linkProvider = new BrokerLinkProvider();
        linkProvider.setContentIsCompressed("false");
        return linkProvider;
    }

    @Bean
    public BrokerPageProvider pageProvider() {
        BrokerPageProvider pageProvider = new BrokerPageProvider();
        pageProvider.setContentIsCompressed("false");
        return pageProvider;
    }

    @Bean
    public BrokerTaxonomyProvider taxonomyProvider() {
        return new BrokerTaxonomyProvider();
    }

    @Bean
    public DynamicMetaRetriever dynamicMetaRetriever() {
        return new DynamicMetaRetriever();
    }

    @Bean
    public BinaryFactory binaryFactory() {
        return new BinaryFactory();
    }

    @Bean
    public ComponentPresentationFactory componentPresentationFactory() {
        ComponentPresentationFactoryImpl presentationFactory = ComponentPresentationFactoryImpl.getInstance();
        presentationFactory.setComponentPresentationProvider(componentPresentationProvider());
        presentationFactory.setCacheProvider(ehCacheProvider);
        return presentationFactory;
    }

    @Bean
    public ComponentPresentationProvider componentPresentationProvider() {
        BrokerComponentPresentationProvider componentPresentationProvider = new BrokerComponentPresentationProvider();
        componentPresentationProvider.setContentIsCompressed("false");
        componentPresentationProvider.setCacheProvider(ehCacheProvider);
        return componentPresentationProvider;
    }
}

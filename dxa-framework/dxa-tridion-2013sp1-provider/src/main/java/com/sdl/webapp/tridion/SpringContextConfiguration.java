package com.sdl.webapp.tridion;

import com.tridion.content.BinaryFactory;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.factories.impl.ComponentPresentationFactoryImpl;
import org.dd4t.providers.ComponentPresentationProvider;
import org.dd4t.providers.PayloadCacheProvider;
import org.dd4t.providers.impl.BrokerLinkProvider;
import org.dd4t.providers.impl.BrokerPageProvider;
import org.dd4t.providers.impl.BrokerTaxonomyProvider;
import org.dd4t.providers.impl.NoCacheProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

@Configuration
@ImportResource("classpath:com/sdl/context/engine/repository/beans-sdl-context-engine.xml")
@ComponentScan("com.sdl.webapp.tridion")
//todo dxa2 move to com.sdl.dxa
public class SpringContextConfiguration {

    @Autowired
    private PayloadCacheProvider ehCacheProvider;

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

    @Configuration
    @Profile("dxa.no-cache")
    public static class NoCacheConfiguration {
        @Bean
        public PayloadCacheProvider cacheProvider() {
            return new NoCacheProvider();
        }
    }
}

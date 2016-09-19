package com.sdl.webapp.tridion;

import com.sdl.context.engine.repository.ContextRepositoryManager;
import com.tridion.content.BinaryFactory;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.taxonomies.TaxonomyFactory;
import com.tridion.taxonomies.TaxonomyRelationManager;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.factories.impl.ComponentPresentationFactoryImpl;
import org.dd4t.providers.ComponentPresentationProvider;
import org.dd4t.providers.PayloadCacheProvider;
import org.dd4t.providers.impl.BrokerLinkProvider;
import org.dd4t.providers.impl.BrokerPageProvider;
import org.dd4t.providers.impl.BrokerTaxonomyProvider;
import org.dd4t.providers.impl.NoCacheProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.File;

@Configuration
@ImportResource("classpath:com/sdl/context/engine/repository/beans-sdl-context-engine.xml")
@ComponentScan("com.sdl.webapp.tridion")
@Slf4j
//todo dxa2 move to com.sdl.dxa
public class SpringContextConfiguration {

    @Value("${dxa.tridion.2013.context.repository.url}")
    private String contextRepositoryUrl;

    @Value("${dxa.tridion.2013.context.repository.location}")
    private String contextRepositoryLocation;

    @Value("${dxa.tridion.2013.context.repository.load.enabled}")
    private Boolean contextRepositoryLoad;

    @Autowired
    private PayloadCacheProvider ehCacheProvider;

    @Autowired
    private ServletContext servletContext;

    @PostConstruct
    public void init() {
        if (contextRepositoryLoad) {
            loadCwdRepository();
        }
    }

    private void loadCwdRepository() {
        String repositoryLocation = System.getProperty("repository.location", contextRepositoryLocation);
        if (!new File(repositoryLocation).isAbsolute()) {
            repositoryLocation = new File(repositoryLocation).getAbsolutePath();
            log.debug("CWD repository URL is not in System properties (which is=[{}]) is null or relative, set {}",
                    System.getProperty("repository.location"), repositoryLocation);
            System.setProperty("repository.location", repositoryLocation);
        }

        log.debug("Using {} CWD repository location", repositoryLocation);

        if (!new File(repositoryLocation).exists()) {
            log.info("Initialization of ContextRepositoryManager#main({}), FileCheck.repository.location: {}, System.repository.location: {}",
                    contextRepositoryUrl, repositoryLocation, System.getProperty("repository.location"));

            ContextRepositoryManager.main(new String[]{contextRepositoryUrl});
        } else {
            log.info("CWD repository {} doesn't need to be updated", repositoryLocation);
        }
    }

    @Bean
    public TaxonomyFactory taxonomyFactory() {
        return new TaxonomyFactory();
    }

    @Bean
    public TaxonomyRelationManager taxonomyRelationManager() {
        return new TaxonomyRelationManager();
    }
    
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

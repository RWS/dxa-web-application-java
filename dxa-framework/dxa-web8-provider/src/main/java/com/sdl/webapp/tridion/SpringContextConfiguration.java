package com.sdl.webapp.tridion;

import com.sdl.context.odata.client.api.ODataContextEngine;
import com.tridion.content.BinaryFactory;
import com.tridion.dynamiccontent.DynamicMappingsRetriever;
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
/**
 * <p>SpringContextConfiguration class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class SpringContextConfiguration {

    //should come from dxa-tridion-provider
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EHCacheProvider ehCacheProvider;

    /**
     * <p>linkProvider.</p>
     *
     * @return a {@link org.dd4t.providers.impl.BrokerLinkProvider} object.
     */
    @Bean
    public BrokerLinkProvider linkProvider() {
        BrokerLinkProvider linkProvider = new BrokerLinkProvider();
        linkProvider.setContentIsCompressed("false");
        return linkProvider;
    }

    /**
     * <p>pageProvider.</p>
     *
     * @return a {@link org.dd4t.providers.impl.BrokerPageProvider} object.
     */
    @Bean
    public BrokerPageProvider pageProvider() {
        BrokerPageProvider pageProvider = new BrokerPageProvider();
        pageProvider.setContentIsCompressed("false");
        return pageProvider;
    }

    /**
     * <p>taxonomyProvider.</p>
     *
     * @return a {@link org.dd4t.providers.impl.BrokerTaxonomyProvider} object.
     */
    @Bean
    public BrokerTaxonomyProvider taxonomyProvider() {
        return new BrokerTaxonomyProvider();
    }

    /**
     * <p>componentPresentationFactory.</p>
     *
     * @return a {@link org.dd4t.core.factories.ComponentPresentationFactory} object.
     */
    @Bean
    public ComponentPresentationFactory componentPresentationFactory() {
        ComponentPresentationFactoryImpl presentationFactory = ComponentPresentationFactoryImpl.getInstance();
        presentationFactory.setComponentPresentationProvider(componentPresentationProvider());
        presentationFactory.setCacheProvider(ehCacheProvider);
        return presentationFactory;
    }

    /**
     * <p>componentPresentationProvider.</p>
     *
     * @return a {@link org.dd4t.providers.ComponentPresentationProvider} object.
     */
    @Bean
    public ComponentPresentationProvider componentPresentationProvider() {
        BrokerComponentPresentationProvider componentPresentationProvider = new BrokerComponentPresentationProvider();
        componentPresentationProvider.setContentIsCompressed("false");
        componentPresentationProvider.setCacheProvider(ehCacheProvider);
        return componentPresentationProvider;
    }

    /**
     * <p>dynamicMetaRetriever.</p>
     *
     * @return a {@link com.tridion.dynamiccontent.DynamicMetaRetriever} object.
     */
    @Bean
    public DynamicMetaRetriever dynamicMetaRetriever() {
        return new DynamicMetaRetriever();
    }

    /**
     * <p>binaryFactory.</p>
     *
     * @return a {@link com.tridion.content.BinaryFactory} object.
     */
    @Bean
    public BinaryFactory binaryFactory() {
        return new BinaryFactory();
    }

    /**
     * <p>dynamicMappingsRetriever.</p>
     *
     * @return a {@link com.tridion.dynamiccontent.DynamicMappingsRetriever} object.
     */
    @Bean
    public DynamicMappingsRetriever dynamicMappingsRetriever() {
        return new DynamicMappingsRetriever();
    }

    @Bean
    public ODataContextEngine oDataContextEngine() {
        return new ODataContextEngine();
    }
}

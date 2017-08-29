package com.sdl.dxa.dd4t;

import com.sdl.dxa.dd4t.providers.ModelServiceComponentPresentationProvider;
import com.sdl.dxa.dd4t.providers.ModelServicePageProvider;
import org.dd4t.core.factories.impl.ComponentPresentationFactoryImpl;
import org.dd4t.core.factories.impl.PageFactoryImpl;
import org.dd4t.providers.PayloadCacheProvider;
import org.dd4t.providers.impl.BrokerComponentPresentationProvider;
import org.dd4t.providers.impl.BrokerPageProvider;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
@Profile("!custom.dd4t.ms.provider")
public class DropInExperienceConfiguration {

    private static final Logger log = getLogger(DropInExperienceConfiguration.class);

    private final PageFactoryImpl pageFactoryImpl;

    private final ComponentPresentationFactoryImpl componentPresentationFactory;

    private final BrokerPageProvider brokerPageProvider;

    private final BrokerComponentPresentationProvider componentPresentationProvider;

    private final PayloadCacheProvider payloadCacheProvider;

    @Autowired(required = false)
    public DropInExperienceConfiguration(PageFactoryImpl pageFactoryImpl,
                                         ComponentPresentationFactoryImpl componentPresentationFactory,
                                         BrokerPageProvider brokerPageProvider,
                                         BrokerComponentPresentationProvider componentPresentationProvider,
                                         PayloadCacheProvider payloadCacheProvider) {
        this.pageFactoryImpl = pageFactoryImpl;
        this.componentPresentationFactory = componentPresentationFactory;
        this.brokerPageProvider = brokerPageProvider;
        this.componentPresentationProvider = componentPresentationProvider;
        this.payloadCacheProvider = payloadCacheProvider;
    }

    @PostConstruct
    public void init() {
        if (pageFactoryImpl != null && brokerPageProvider != null &&
                componentPresentationFactory != null && componentPresentationProvider != null) {
            pageFactoryImpl.setPageProvider(modelServicePageProvider());
            componentPresentationFactory.setComponentPresentationProvider(modelServiceComponentPresentationProvider());
            log.info("Default DD4T Page/CP Providers have been replaced with default DXA Model Service Page/CP Providers. " +
                    "Run application with 'custom.dd4t.ms.provider' Spring profile to use any custom beans configuration.");
        }
    }

    @Bean
    @Primary
    public ModelServicePageProvider modelServicePageProvider() {
        ModelServicePageProvider provider = new ModelServicePageProvider();
        provider.setContentIsCompressed("false");
        provider.setContentIsBase64Encoded(false);
        provider.setCacheProvider(payloadCacheProvider);
        return provider;
    }

    @Bean
    @Primary
    public ModelServiceComponentPresentationProvider modelServiceComponentPresentationProvider() {
        ModelServiceComponentPresentationProvider provider = new ModelServiceComponentPresentationProvider();
        provider.setContentIsCompressed("false");
        provider.setContentIsBase64Encoded(false);
        provider.setCacheProvider(payloadCacheProvider);
        return provider;
    }
}

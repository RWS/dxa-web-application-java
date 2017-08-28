package com.sdl.dxa.dd4t;

import com.sdl.dxa.dd4t.providers.ModelServicePageProvider;
import org.dd4t.core.factories.impl.PageFactoryImpl;
import org.dd4t.providers.PayloadCacheProvider;
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

    private final BrokerPageProvider brokerPageProvider;

    private final PayloadCacheProvider payloadCacheProvider;

    @Autowired(required = false)
    public DropInExperienceConfiguration(PageFactoryImpl pageFactoryImpl,
                                         BrokerPageProvider brokerPageProvider,
                                         PayloadCacheProvider payloadCacheProvider) {
        this.pageFactoryImpl = pageFactoryImpl;
        this.brokerPageProvider = brokerPageProvider;
        this.payloadCacheProvider = payloadCacheProvider;
    }

    @PostConstruct
    public void init() {
        if (pageFactoryImpl != null && brokerPageProvider != null) {
            pageFactoryImpl.setPageProvider(modelServicePageProvider());
            log.info("Default Broker Page Provider has been replaced with default DXA Model Service Page Provider. " +
                    "Run application with 'custom.dd4t.ms.provider' Spring profile to use any custom beans configuration.");
        }
    }

    @Bean
    @Primary
    public ModelServicePageProvider modelServicePageProvider() {
        ModelServicePageProvider modelServicePageProvider = new ModelServicePageProvider();
        modelServicePageProvider.setContentIsBase64Encoded(false);
        modelServicePageProvider.setContentIsCompressed("false");
        modelServicePageProvider.setCacheProvider(payloadCacheProvider);
        return modelServicePageProvider;
    }
}

package com.sdl.dxa.dd4t;

import com.sdl.dxa.dd4t.providers.ModelServiceComponentPresentationProvider;
import com.sdl.dxa.dd4t.providers.ModelServicePageProvider;
import com.sdl.dxa.tridion.modelservice.ModelServiceClient;
import com.sdl.dxa.tridion.modelservice.ModelServiceConfiguration;
import org.dd4t.core.factories.impl.ComponentPresentationFactoryImpl;
import org.dd4t.core.factories.impl.PageFactoryImpl;
import org.dd4t.providers.PayloadCacheProvider;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@Configuration
@Profile("auto.dd4t.ms.provider")
public class DropInExperienceConfiguration implements ApplicationContextAware {

    private static final Logger log = getLogger(DropInExperienceConfiguration.class);

    @Autowired
    private ModelServiceClient modelServiceClient;

    @Autowired
    private ModelServiceConfiguration modelServiceConfiguration;

    public DropInExperienceConfiguration() {
        // required empty
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        PageFactoryImpl pageFactory = applicationContext.getBean(PageFactoryImpl.class);
        ComponentPresentationFactoryImpl componentPresentationFactory = applicationContext.getBean(ComponentPresentationFactoryImpl.class);

        PayloadCacheProvider cacheProvider = applicationContext.getBean(PayloadCacheProvider.class);

        ModelServicePageProvider modelServicePageProvider = modelServicePageProvider(cacheProvider);

        ModelServiceComponentPresentationProvider modelServiceComponentPresentationProvider = componentPresentationProvider(cacheProvider);

        if (pageFactory != null && componentPresentationFactory != null) {
            pageFactory.setPageProvider(modelServicePageProvider);
            componentPresentationFactory.setComponentPresentationProvider(modelServiceComponentPresentationProvider);
            log.info("Default DD4T Page/CP Providers have been replaced with default DXA Model Service Page/CP Providers. " +
                    "Run application without 'auto.dd4t.ms.provider' Spring profile to use any custom beans configuration.");
        }
    }

    private ModelServiceComponentPresentationProvider componentPresentationProvider(PayloadCacheProvider cacheProvider) {
        ModelServiceComponentPresentationProvider modelServiceComponentPresentationProvider = new ModelServiceComponentPresentationProvider();
        modelServiceComponentPresentationProvider.setContentIsCompressed("false");
        modelServiceComponentPresentationProvider.setCacheProvider(cacheProvider);
        modelServiceComponentPresentationProvider.setModelServiceClient(modelServiceClient);
        modelServiceComponentPresentationProvider.setModelServiceConfiguration(modelServiceConfiguration);
        return modelServiceComponentPresentationProvider;
    }

    private ModelServicePageProvider modelServicePageProvider(PayloadCacheProvider cacheProvider) {
        ModelServicePageProvider modelServicePageProvider = new ModelServicePageProvider();
        modelServicePageProvider.setContentIsCompressed("false");
        modelServicePageProvider.setCacheProvider(cacheProvider);
        modelServicePageProvider.setModelServiceClient(modelServiceClient);
        modelServicePageProvider.setModelServiceConfiguration(modelServiceConfiguration);
        return modelServicePageProvider;
    }
}

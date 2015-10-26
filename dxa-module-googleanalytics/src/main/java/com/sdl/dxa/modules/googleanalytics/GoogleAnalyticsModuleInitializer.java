package com.sdl.dxa.modules.googleanalytics;

import com.sdl.dxa.modules.googleanalytics.model.GoogleAnalyticsConfiguration;
import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.Configuration;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.impl.AbstractInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GoogleAnalyticsModuleInitializer extends AbstractInitializer {


    @Autowired
    private SemanticMappingRegistry semanticMappingRegistry;

    @Autowired
    public GoogleAnalyticsModuleInitializer(ViewModelRegistry viewModelRegistry) {
        super(viewModelRegistry, "GoogleAnalytics");
    }

    @PostConstruct
    public void initialize() throws Exception {
        this.registerViewModel("GoogleAnalytics", GoogleAnalyticsConfiguration.class);
    }
}

package com.sdl.dxa.modules.googleanalytics;

import com.sdl.dxa.modules.googleanalytics.model.GoogleAnalyticsConfiguration;
import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.Configuration;
import com.sdl.webapp.common.exceptions.DxaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GoogleAnalyticsModuleInitializer {

    @Autowired
    private ViewModelRegistry viewModelRegistry;

    @Autowired
    private SemanticMappingRegistry semanticMappingRegistry;


    @PostConstruct
    public void initialize() throws Exception {
        try {
            viewModelRegistry.registerViewEntityClass("GoogleAnalytics:GoogleAnalytics", GoogleAnalyticsConfiguration.class);
        } catch (DxaException e) {
            e.printStackTrace();
        }
    }
}

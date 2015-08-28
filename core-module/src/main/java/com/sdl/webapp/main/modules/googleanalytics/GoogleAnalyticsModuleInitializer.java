package com.sdl.webapp.main.modules.googleanalytics;

import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GoogleAnalyticsModuleInitializer {

    private final ViewModelRegistry viewModelRegistry;

    @Autowired
    public GoogleAnalyticsModuleInitializer(ViewModelRegistry viewModelRegistry) {
        this.viewModelRegistry = viewModelRegistry;
    }

    @PostConstruct
    public void registerViewModelEntityClasses() {
        viewModelRegistry.registerViewEntityClass("GoogleAnalytics:GoogleAnalytics", Configuration.class);
    }
}

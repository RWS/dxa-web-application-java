package com.sdl.webapp.main.modules.googleanalytics;

import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.Link;
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
        // TODO: Implement this for real, currently this is just a dummy implementation to avoid errors
        viewModelRegistry.registerViewEntityClass("GoogleAnalytics:GoogleAnalytics", Link.class);
    }
}

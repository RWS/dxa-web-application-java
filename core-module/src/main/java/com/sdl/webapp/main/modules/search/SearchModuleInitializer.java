package com.sdl.webapp.main.modules.search;

import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SearchModuleInitializer {

    private final ViewModelRegistry viewModelRegistry;

    @Autowired
    public SearchModuleInitializer(ViewModelRegistry viewModelRegistry) {
        this.viewModelRegistry = viewModelRegistry;
    }

    @PostConstruct
    public void registerViewModelEntityClasses() {
        // TODO: Implement this for real, currently this is just a dummy implementation to avoid errors
        viewModelRegistry.registerViewEntityClass("Search:SearchBox", Link.class);
    }
}

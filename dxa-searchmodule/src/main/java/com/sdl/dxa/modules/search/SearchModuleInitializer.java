package com.sdl.dxa.modules.search;

import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.impl.AbstractInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SearchModuleInitializer extends AbstractInitializer {

    @Autowired
    public SearchModuleInitializer(ViewModelRegistry viewModelRegistry) {
        super(viewModelRegistry, "Search");
    }

    @PostConstruct
    public void initialize() {
        // TODO: Implement this for real, currently this is just a dummy implementation to avoid errors

        this.registerViewModel("SearchBox", Link.class);

    }
}

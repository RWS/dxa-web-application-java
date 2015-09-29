package com.sdl.dxa.modules.example;

import com.sdl.dxa.modules.example.model.CustomPageModelImpl;
import com.sdl.dxa.modules.example.model.VimeoVideo;
import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.Article;
import com.sdl.webapp.common.api.model.page.PageModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * TestModuleInitializer
 *
 */
@Component
public class TestModuleInitializer {

    @Autowired
    private ViewModelRegistry viewModelRegistry;

    @Autowired
    private SemanticMappingRegistry semanticMappingRegistry;

    @PostConstruct
    public void initialize() {

        try {
            this.viewModelRegistry.registerViewEntityClass("Test:VimeoVideo", VimeoVideo.class);
            this.viewModelRegistry.registerViewEntityClass("Test:ShowClaims", Article.class);
            this.viewModelRegistry.registerViewEntityClass("Test:ShowClaims", Article.class);
            this.viewModelRegistry.registerPageViewModel("Test:GeneralPageCustomRegion", CustomPageModelImpl.class);
            this.viewModelRegistry.registerRegionViewModel("Test:CustomRegion", RegionModelImpl.class);
        } catch (DxaException e) {
            e.printStackTrace();
        }

        this.semanticMappingRegistry.registerEntity(VimeoVideo.class);

    }
}

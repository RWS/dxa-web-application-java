package com.sdl.dxa.modules.test;

import com.sdl.dxa.modules.test.model.CustomPageModelImpl;
import com.sdl.dxa.modules.test.model.CustomRegionModelImpl;
import com.sdl.dxa.modules.test.model.ExternalContentLibraryStubSchemaflickr;
import com.sdl.dxa.modules.test.model.VimeoVideo;
import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.Article;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.impl.mapping.MvcDataImpl;
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
            this.viewModelRegistry.registerViewEntityClass("Test:CustomPageMetadata", CustomPageModelImpl.class);
            this.viewModelRegistry.registerRegionViewModel("Test:CustomRegion", CustomRegionModelImpl.class);
            this.viewModelRegistry.registerViewEntityClass("Test:ExternalContentLibraryStubSchemaflickr", ExternalContentLibraryStubSchemaflickr.class);
        } catch (DxaException e) {
            e.printStackTrace();
        }

        this.semanticMappingRegistry.registerEntity(VimeoVideo.class);

    }
}

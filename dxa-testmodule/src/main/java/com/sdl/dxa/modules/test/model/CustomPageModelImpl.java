package com.sdl.dxa.modules.test.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.page.PageModelImpl;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

/**
 * Created by Administrator on 28/09/2015.
 */
@SemanticEntity(entityName = "CustomPageMetadata", vocabulary = SDL_CORE, prefix = "m")
public class CustomPageModelImpl extends PageModelImpl {

    @SemanticProperty("m:headline")
    private String headline;


    public String getHeadline()
    {
        return this.headline;
    }
}

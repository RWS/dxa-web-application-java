package com.sdl.dxa.modules.test.model;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

@SemanticEntity(entityName = "CustomModel", vocabulary = SDL_CORE, prefix = "m")
public class CustomModel extends AbstractEntityModel {

    @SemanticProperty("m:myCustomFieldValue")
    private String myCustomFieldValue;

    public String getMyCustomFieldValue() {
        return this.myCustomFieldValue;
    }
}

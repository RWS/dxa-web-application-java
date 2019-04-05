package com.sdl.dxa.tridion.models.topic;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;

@SemanticEntity(vocabulary = SemanticVocabulary.SDL_DITA)
public class TestSpecializedSection extends AbstractEntityModel {
    @SemanticProperty("_self")
    public RichText content;

    public RichText getContent() {
        return content;
    }

    public void setContent(RichText content) {
        this.content = content;
    }
}

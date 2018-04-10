package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.dxa.common.util.PathUtils;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

/**
 * @dxa.publicApi
 */
@SemanticEntity(entityName = "EmbeddedLink", vocabulary = SDL_CORE, prefix = "e")
@Data
@EqualsAndHashCode(callSuper = true)
public class Link extends AbstractEntityModel {

    @SemanticProperties({
            @SemanticProperty("internalLink"),
            @SemanticProperty("externalLink"),
            @SemanticProperty("e:internalLink"),
            @SemanticProperty("e:externalLink")
    })
    @JsonProperty("Url")
    private String url;

    @JsonProperty("LinkText")
    @SemanticProperty("e:linkText")
    private String linkText;

    @SemanticProperty("e:alternateText")
    @JsonProperty("AlternateText")
    private String alternateText;

    /**
     * Decides whether this request path is in context of this link.
     *
     * @param requestPath  current request path
     * @param localization current localization
     * @return whether this request path is in context of this link
     */
    public boolean isCurrentContext(String requestPath, Localization localization) {
        return PathUtils.isActiveContextPath(requestPath, localization.getPath(), getUrl());
    }

}

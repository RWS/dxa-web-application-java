package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;


import java.util.Map;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

/**
 * GenericWidget
 *
 * @author nic
 */
@SemanticEntity(entityName = "GenericWidget", vocabulary = SDL_CORE, prefix = "gw", public_ = true)
public class GenericWidget extends AbstractEntity {

    @SemanticProperty("gw:parameters")
    private Map<String,String> parameters;

    // Link to same widget with "full" CT
    //
    @SemanticProperty("gw:_self")
    private String link;

    // TODO: Introduce also named links so the widget can find links to other widgets

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "GenericWidget{" +
                "parameters=" + parameters +
                ",link=" + link +
                '}';
    }
}

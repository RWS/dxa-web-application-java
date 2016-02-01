package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;

import java.util.Map;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

/**
 * GenericWidget
 *
 * @author nic
 * @version 1.3-SNAPSHOT
 */
@SemanticEntity(entityName = "GenericWidget", vocabulary = SDL_CORE, prefix = "gw", public_ = true)
public class GenericWidget extends AbstractEntityModel {

    @SemanticProperty("gw:parameters")
    private Map<String, String> parameters;

    // Link to same widget with "full" CT
    //
    @SemanticProperty("gw:_self")
    private String link;

    // TODO: Introduce also named links so the widget can find links to other widgets

    /**
     * <p>Getter for the field <code>parameters</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * <p>Setter for the field <code>parameters</code>.</p>
     *
     * @param parameters a {@link java.util.Map} object.
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * <p>Getter for the field <code>link</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLink() {
        return link;
    }

    /**
     * <p>Setter for the field <code>link</code>.</p>
     *
     * @param link a {@link java.lang.String} object.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "GenericWidget{" +
                "parameters=" + parameters +
                ",link=" + link +
                '}';
    }
}

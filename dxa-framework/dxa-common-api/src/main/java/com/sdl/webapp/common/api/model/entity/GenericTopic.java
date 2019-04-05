package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;

import java.io.Serializable;

/**
 * Represents a generic Tridion Docs Topic.
 * This is the result of default DXA semantic mapping.
 * Since all the Topic data is rendered as HTML, it may not be the most practical to work with in an MVC Web Application.
 * This generic Topic can be transformed into a user-defined, Strongly Typed Topic Model using an additional Model Builder: the "StronglyTypedTopicBuilder".
 * <p>
 * Although this View Model Type is part of the DXA Framework, it has to be registered like any other View Model Type.
 * In order to work with Tridion Docs content, it will be associated with specific MVC data.
 * A DXA Web Application/Module that wants to work with Tridion Docs content should include this module
 * unless it defines its own View Model Type for generic Topics.
 */
@SemanticEntity("Topic")
public class GenericTopic extends AbstractEntityModel {

    /**
     * Gets or sets the topic title.
     */
    @SemanticProperty("topicTitle")
    private String topicTitle;

    /**
     * Gets or sets the topic body.
     * The topic body is an XHTML fragment which contains _all_ DITA properties (incl. title, body, related-links, nested topics)
     */
    @SemanticProperty("topicBody")
    private String topicBody;

    public GenericTopic() {
    }

    public GenericTopic(String topicTitle, String topicBody) {
        this.topicTitle = topicTitle;
        this.topicBody = topicBody;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getTopicBody() {
        return topicBody;
    }

    public void setTopicBody(String topicBody) {
        this.topicBody = topicBody;
    }

    @Override
    public String toString() {
        return "GenericTopic{" +
                "topicTitle='" + topicTitle + '\'' +
                ", topicBody='" + topicBody + '\'' +
                '}';
    }
}

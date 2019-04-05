package com.sdl.dxa.tridion.models.topic;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;

import java.util.List;

@SemanticEntity(vocabulary = SemanticVocabulary.SDL_DITA, entityName = "body")
// TODO: Support for low-level XPaths: [SemanticEntity(Vocab = XPathVocabulary, EntityName = ".//*[contains(@class, 'body ')]", Prefix ="xpath")]
public class StronglyTypedTopicTest extends AbstractEntityModel {
    @SemanticProperty("_topicTitle")
    public String topicTitle;

    @SemanticProperty("title")
    public String title;

    @SemanticProperty("body")
    public String body;

    @SemanticProperty("body")
    public RichText bodyRichText;

    @SemanticProperty("section")
    public String firstSection;

    @SemanticProperty("section")
    public List<String> sections;

    @SemanticProperty("link")
    public List<Link> links;

    @SemanticProperty("childlink")
    public Link firstChildLink;

    @SemanticProperty("related-links/childlink")
    // TODO: Support for low-level XPaths: [SemanticProperty("xpath:.//*[contains(@class, 'related-links' )]//*[contains(@class, 'childlink' )]")]
    public List<Link> childLinks;

    @Override
    public MvcData getDefaultMvcData() {
        return MvcDataImpl.newBuilder().areaName("Test").controllerName("Entity").viewName("StronglyTypedTopicTest").build();
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public RichText getBodyRichText() {
        return bodyRichText;
    }

    public void setBodyRichText(RichText bodyRichText) {
        this.bodyRichText = bodyRichText;
    }

    public String getFirstSection() {
        return firstSection;
    }

    public void setFirstSection(String firstSection) {
        this.firstSection = firstSection;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Link getFirstChildLink() {
        return firstChildLink;
    }

    public void setFirstChildLink(Link firstChildLink) {
        this.firstChildLink = firstChildLink;
    }

    public List<Link> getChildLinks() {
        return childLinks;
    }

    public void setChildLinks(List<Link> childLinks) {
        this.childLinks = childLinks;
    }
}

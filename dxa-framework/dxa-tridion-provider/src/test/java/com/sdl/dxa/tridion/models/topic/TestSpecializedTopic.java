package com.sdl.dxa.tridion.models.topic;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;

@SemanticEntity(vocabulary = SemanticVocabulary.SDL_DITA, entityName = "lcBaseBody")
public class TestSpecializedTopic extends AbstractEntityModel {
    @SemanticProperty("title")
    public String title;

    @SemanticProperty("lcIntro")
    public RichText intro;

    @SemanticProperty("lcObjectives")
    public RichText objectives;

    @SemanticProperty("lcBaseBody")
    public TestSpecializedBody body;

    @Override
    public MvcData getDefaultMvcData() {
        return MvcDataImpl.newBuilder().areaName("Test").controllerName("Topic").viewName("TestSpecializedTopic").build();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RichText getIntro() {
        return intro;
    }

    public void setIntro(RichText intro) {
        this.intro = intro;
    }

    public RichText getObjectives() {
        return objectives;
    }

    public void setObjectives(RichText objectives) {
        this.objectives = objectives;
    }

    public TestSpecializedBody getBody() {
        return body;
    }

    public void setBody(TestSpecializedBody body) {
        this.body = body;
    }
}


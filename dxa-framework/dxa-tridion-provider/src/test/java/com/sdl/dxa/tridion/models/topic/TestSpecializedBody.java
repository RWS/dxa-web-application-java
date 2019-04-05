package com.sdl.dxa.tridion.models.topic;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SemanticEntity(vocabulary = SemanticVocabulary.SDL_DITA)
public class TestSpecializedBody extends AbstractEntityModel {

    @SemanticProperty("lcIntro")
    public RichText intro;

    @SemanticProperty("lcObjectives")
    public TestSpecializedSection objectives;

    @SemanticProperty("section")
    public List<TestSpecializedSection> sections;

    @Override
    public @Nullable MvcData getDefaultMvcData() {
        return MvcDataImpl.newBuilder().areaName("Test").controllerName("Entity").viewName("TestSpecializedBody").build();
    }

    public RichText getIntro() {
        return intro;
    }

    public void setIntro(RichText intro) {
        this.intro = intro;
    }

    public TestSpecializedSection getObjectives() {
        return objectives;
    }

    public void setObjectives(TestSpecializedSection objectives) {
        this.objectives = objectives;
    }

    public List<TestSpecializedSection> getSections() {
        return sections;
    }

    public void setSections(List<TestSpecializedSection> sections) {
        this.sections = sections;
    }
}

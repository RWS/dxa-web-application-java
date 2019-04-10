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
public class SpecializedBodyTest extends AbstractEntityModel {

    @SemanticProperty("lcIntro")
    public RichText intro;

    @SemanticProperty("lcObjectives")
    public SpecializedSectionTest objectives;

    @SemanticProperty("section")
    public List<SpecializedSectionTest> sections;

    @Override
    public @Nullable MvcData getDefaultMvcData() {
        return MvcDataImpl.newBuilder().areaName("Test").controllerName("Entity").viewName("SpecializedBodyTest").build();
    }

    public RichText getIntro() {
        return intro;
    }

    public void setIntro(RichText intro) {
        this.intro = intro;
    }

    public SpecializedSectionTest getObjectives() {
        return objectives;
    }

    public void setObjectives(SpecializedSectionTest objectives) {
        this.objectives = objectives;
    }

    public List<SpecializedSectionTest> getSections() {
        return sections;
    }

    public void setSections(List<SpecializedSectionTest> sections) {
        this.sections = sections;
    }
}

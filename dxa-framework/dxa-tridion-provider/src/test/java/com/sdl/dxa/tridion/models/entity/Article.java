package com.sdl.dxa.tridion.models.entity;

import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import org.joda.time.DateTime;

@SemanticEntity(entityName = "Article", prefix = "s",public_ = true)
public class Article extends AbstractEntityModel {
        @SemanticProperty("s:headline")
        public String headline;
        @SemanticProperty("s:image")
        public Image image;
        @SemanticProperty("s:dateCreated")
        public DateTime date;
        @SemanticProperty("s:about")
        public String description;
}

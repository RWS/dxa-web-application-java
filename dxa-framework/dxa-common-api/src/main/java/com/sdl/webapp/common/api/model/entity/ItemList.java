package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

@SemanticEntity(entityName = "ItemList", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class ItemList extends AbstractEntityModel {

    @JsonProperty("Headline")
    @SemanticProperty("s:headline")
    private String headline;

    @JsonProperty("ItemListElement")
    @SemanticProperty("s:itemListElement")
    private List<Teaser> itemListElements;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public List<Teaser> getItemListElements() {
        return itemListElements;
    }

    public void setItemListElements(List<Teaser> itemListElements) {
        this.itemListElements = itemListElements;
    }

    @Override
    public String toString() {
        return "ItemList{" +
                "headline='" + headline + '\'' +
                ", itemListElements=" + itemListElements +
                '}';
    }
}

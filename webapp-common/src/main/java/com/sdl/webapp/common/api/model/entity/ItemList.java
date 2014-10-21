package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.SemanticEntity;
import com.sdl.webapp.common.api.mapping.SemanticProperty;
import com.sdl.webapp.common.api.mapping.Vocabularies;

import java.util.List;

@SemanticEntity(entityName = "ItemList", vocab = Vocabularies.SCHEMA_ORG, prefix = "s", pub = true)
public class ItemList extends EntityBase {

    @SemanticProperty("s:headline")
    private String headline;

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
}

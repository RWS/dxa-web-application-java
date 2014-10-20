package com.sdl.webapp.common.model.entity;

import com.sdl.webapp.common.mapping.SemanticEntity;
import com.sdl.webapp.common.mapping.SemanticProperty;

import java.util.List;

@SemanticEntity(entityName = "ItemList", vocab = "http://schema.org", prefix = "s", pub = true)
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

package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;

import java.util.List;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;

/**
 * <p>ItemList class.</p>
 */
@SemanticEntity(entityName = "ItemList", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class ItemList extends AbstractEntityModel {

    @JsonProperty("Headline")
    @SemanticProperty("s:headline")
    private String headline;

    // single/plural ending explanation: schema.org & CM say it is element (single) because CM operates with them as with
    // single entities.
    // So since we actually have multiple elements it was decided to call it elements (plural) in DXA.
    @JsonProperty("ItemListElements")
    @SemanticProperty("s:itemListElement")
    private List<Teaser> itemListElements;

    /**
     * <p>Getter for the field <code>headline</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * <p>Setter for the field <code>headline</code>.</p>
     *
     * @param headline a {@link java.lang.String} object.
     */
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    /**
     * <p>Getter for the field <code>itemListElements</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Teaser> getItemListElements() {
        return itemListElements;
    }

    /**
     * <p>Setter for the field <code>itemListElements</code>.</p>
     *
     * @param itemListElements a {@link java.util.List} object.
     */
    public void setItemListElements(List<Teaser> itemListElements) {
        this.itemListElements = itemListElements;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ItemList{" +
                "headline='" + headline + '\'' +
                ", itemListElements=" + itemListElements +
                '}';
    }
}

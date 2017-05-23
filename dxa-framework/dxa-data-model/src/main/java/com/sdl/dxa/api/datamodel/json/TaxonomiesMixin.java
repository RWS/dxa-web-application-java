package com.sdl.dxa.api.datamodel.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.util.Set;

public interface TaxonomiesMixin {

    @JsonProperty("Key")
    String getKey();

    @JsonProperty("Description")
    String getDescription();

    @JsonProperty("IsAbstract")
    boolean isTaxonomyAbstract();

    @JsonProperty("HasChildNodes")
    boolean isWithChildren();

    @JsonProperty("ClassifiedItemsCount")
    int getClassifiedItemsCount();

    @JsonProperty("Id")
    String getId();

    @JsonProperty("Type")
    String getType();

    @JsonProperty("Title")
    String getTitle();

    @JsonProperty("Url")
    String getUrl();

    @JsonIgnore
    String getOriginalTitle();

    @JsonProperty("Visible")
    boolean isVisible();

    @JsonProperty("Items")
    Set<Object> getItems();

    @JsonProperty("PublishedDate")
    DateTime getPublishedDate();
}

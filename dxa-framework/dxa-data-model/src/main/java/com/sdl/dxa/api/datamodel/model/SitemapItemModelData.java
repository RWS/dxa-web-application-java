package com.sdl.dxa.api.datamodel.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class SitemapItemModelData {

    private String id;

    private String type;

    private String title;

    private String url;

    private boolean visible;

    private Set<SitemapItemModelData> items;

    private DateTime publishedDate;

    //todo wrap with sorted set?

    /**
     * Adds an item to a collection of items and initializes it if needed.
     *
     * @param item item to add
     * @return itself
     */
    @NotNull
    public SitemapItemModelData addItem(SitemapItemModelData item) {
        if (this.items == null) {
            this.items = new LinkedHashSet<>();
        }
        this.items.add(item);
        return this;
    }
}

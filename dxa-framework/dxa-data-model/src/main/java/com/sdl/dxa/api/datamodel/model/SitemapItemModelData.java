package com.sdl.dxa.api.datamodel.model;

import com.google.common.collect.ComparisonChain;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.util.SortedSet;
import java.util.TreeSet;

@Data
@Accessors(chain = true)
public class SitemapItemModelData implements Comparable<SitemapItemModelData> {

    private String id;

    private String type;

    private String title;

    @Setter(value = AccessLevel.PRIVATE)
    private String originalTitle;

    private String url;

    private boolean visible;

    private SortedSet<SitemapItemModelData> items = new TreeSet<>();

    private DateTime publishedDate;

    /**
     * Adds an item to a collection of items and initializes it if needed.
     *
     * @param item item to add
     * @return itself
     */
    @NotNull
    public SitemapItemModelData addItem(SitemapItemModelData item) {
        if (this.items == null) {
            this.items = new TreeSet<>();
        }
        this.items.add(item);
        return this;
    }

    @Override
    public int compareTo(@NotNull SitemapItemModelData o) {
        return ComparisonChain.start()
                .compare(this.getOriginalTitle(), o.getOriginalTitle())
                .compare(this.getId(), o.getId())
                .result();
    }

    /**
     * Setter for title which also sets original title <strong>if original title is not yet set</strong>.
     *
     * @param title title to set
     */
    public SitemapItemModelData setTitle(String title) {
        this.title = title;
        if (this.originalTitle == null) {
            setOriginalTitle(title);
        }
        return this;
    }
}

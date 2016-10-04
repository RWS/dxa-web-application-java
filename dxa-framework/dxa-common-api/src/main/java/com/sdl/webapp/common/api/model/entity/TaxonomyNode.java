package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ComparisonChain;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.comparator.NullSafeComparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a special kind of {@link SitemapItem} which is used for Taxonomy Nodes.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaxonomyNode extends SitemapItem {

    private static final Comparator<SitemapItem> SITEMAP_SORT_BY_TITLE_AND_ID = new NullSafeComparator<>(new Comparator<SitemapItem>() {
        @Override
        public int compare(SitemapItem o1, SitemapItem o2) {
            return ComparisonChain.start()
                    .compare(o1.getOriginalTitle(), o2.getOriginalTitle())
                    .compare(o1.getId(), o2.getId())
                    .result();
        }
    }, true);

    @JsonProperty("Key")
    private String key;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("IsAbstract")
    private boolean taxonomyAbstract;

    @JsonProperty("HasChildNodes")
    private boolean withChildren;

    @JsonProperty("ClassifiedItemsCount")
    private int classifiedItemsCount;

    @NotNull
    @Override
    public List<SitemapItem> getItems() {
        List<SitemapItem> items = super.getItems();
        Collections.sort(items, SITEMAP_SORT_BY_TITLE_AND_ID);
        return items;
    }

    @Override
    @Contract("_, _ -> !null")
    public Link createLink(LinkResolver linkResolver, Localization localization) {
        Link link = super.createLink(linkResolver, localization);
        link.setAlternateText(getDescription());
        return link;
    }
}

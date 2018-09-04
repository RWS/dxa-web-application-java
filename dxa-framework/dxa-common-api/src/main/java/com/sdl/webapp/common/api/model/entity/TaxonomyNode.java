package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ComparisonChain;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.comparator.NullSafeComparator;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a special kind of {@link SitemapItem} which is used for Taxonomy Nodes.
 * @dxa.publicApi
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class TaxonomyNode extends SitemapItem {

    private static final Comparator<SitemapItem> SITEMAP_SORT_BY_TITLE_AND_ID = new NullSafeComparator<>((o1, o2) -> ComparisonChain.start()
            .compare(o1.getOriginalTitle(), o2.getOriginalTitle())
            .compare(o1.getId(), o2.getId())
            .result(), true);

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

    @Override
    protected Set<SitemapItem> wrapItems(@Nullable Set<SitemapItem> items) {
        TreeSet<SitemapItem> treeSet = new TreeSet<>(SITEMAP_SORT_BY_TITLE_AND_ID);
        if (items != null) {
            treeSet.addAll(items);
        }
        return treeSet;
    }

    @Override
    @Contract("_, _ -> !null")
    public Link createLink(LinkResolver linkResolver, Localization localization) {
        Link link = super.createLink(linkResolver, localization);
        link.setAlternateText(getDescription());
        return link;
    }
}

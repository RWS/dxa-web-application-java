package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Contract;

/**
 * Represents a special kind of {@link SitemapItem} which is used for Taxonomy Nodes.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaxonomyNode extends SitemapItem {

    private String key;

    private String description;

    private boolean taxonomyAbstract;

    private boolean withChildren;

    private int classifiedItemsCount;

    @Override
    @Contract("_, _ -> !null")
    public Link createLink(LinkResolver linkResolver, Localization localization) {
        Link link = super.createLink(linkResolver, localization);
        link.setAlternateText(getDescription());
        return link;
    }
}

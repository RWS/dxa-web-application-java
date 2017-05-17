package com.sdl.dxa.api.datamodel.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class TaxonomyNodeModelData extends SitemapItemModelData {

    private String key;

    private boolean withChildren;

    private String description;

    private boolean taxonomyAbstract;

    private int classifiedItemsCount;
}

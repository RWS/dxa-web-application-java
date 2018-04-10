package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.api.formatters.support.FeedItemsProvider;

import java.util.List;

/**
 * Region model is an interface representing one region on a page. Should be implemented by different types of regions.
 * @dxa.publicApi
 */
public interface RegionModel extends ViewModel, FeedItemsProvider, CanFilterEntities {

    String getName();

    /**
     * Returns a list of {@link EntityModel} objects that are inside this region.
     *
     * @return a list of {@link EntityModel}
     */
    List<EntityModel> getEntities();

    /**
     * Gets an {@link EntityModel} by the given id from the whole list {@link #getEntities()}.
     *
     * @param entityId id of {@link EntityModel}
     * @return an {@link EntityModel}, may return null if there is no entity with the given id
     */
    EntityModel getEntity(String entityId);

    /**
     * Return a set of sub-regions of this region.
     *
     * @return a set of sub regions
     */
    RegionModelSet getRegions();

    /**
     * Adds an entity to a list of known entities of this region.
     *
     * @param entity an entity to add
     */
    void addEntity(EntityModel entity);

    RegionModel deepCopy();
}

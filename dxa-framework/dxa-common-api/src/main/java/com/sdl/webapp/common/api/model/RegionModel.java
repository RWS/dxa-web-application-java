package com.sdl.webapp.common.api.model;

import java.util.List;

/**
 * <p>RegionModel interface.</p>
 */
public interface RegionModel extends ViewModel {

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getName();

    /**
     * <p>getEntities.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<EntityModel> getEntities();

    /**
     * <p>getEntity.</p>
     *
     * @param entityId a {@link java.lang.String} object.
     * @return a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     */
    EntityModel getEntity(String entityId);

    /**
     * <p>getRegions.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.RegionModelSet} object.
     */
    RegionModelSet getRegions();

    /**
     * <p>setMvcData.</p>
     *
     * @param value a {@link com.sdl.webapp.common.api.model.MvcData} object.
     */
    void setMvcData(MvcData value);

    /**
     * <p>addEntity.</p>
     *
     * @param entity a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     */
    void addEntity(EntityModel entity);
}

package com.sdl.webapp.common.api.model;

import java.util.Set;

/**
 * <p>RegionModelSet interface.</p>
 * @dxa.publicApi
 */
public interface RegionModelSet extends Set<RegionModel> {

    /**
     * <p>get.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link com.sdl.webapp.common.api.model.RegionModel} object.
     */
    RegionModel get(String name);

    /**
     * <p>get.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @param <T>   a T object.
     * @return a {@link java.util.Set} object.
     */
    <T extends RegionModel> Set<T> get(Class<T> clazz);

    /**
     * <p>containsName.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean containsName(String name);

    /**
     * <p>containsClass.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @return a boolean.
     */
    boolean containsClass(Class<? extends RegionModel> clazz);
}

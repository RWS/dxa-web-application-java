package com.sdl.dxa.caching.wrapper;

import com.sdl.dxa.caching.NeverCached;

/**
 * Interface that determines if the implementor model is cacheable and may be cached.
 */
public interface VolatileModel {

    /**
     * Returns whether the model is cacheable and may be cached.
     *
     * @return whether the model never may be cached
     */
    default boolean canBeCached() {
        return !this.getClass().isAnnotationPresent(NeverCached.class) && isStaticModel();
    }

    /**
     * Returns whether the model is static.
     *
     * @return whether the model is static
     */
    default boolean isStaticModel() {
        return false;
    }

    /**
     * Set if the model is static.
     *
     * @param staticModel if the model is static
     */
    default void setStaticModel(boolean staticModel) {
        // default implementation does nothing
    }
}

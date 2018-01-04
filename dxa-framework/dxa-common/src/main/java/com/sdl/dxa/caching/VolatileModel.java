package com.sdl.dxa.caching;

/**
 * Interface that determines if the implementor model is cacheable and may be cached.
 *
 * @dxa.publicApi
 */
public interface VolatileModel {

    /**
     * Returns whether the model is cacheable and may be cached.
     *
     * @return whether the model never may be cached
     * @dxa.publicApi
     */
    default boolean canBeCached() {
        return !this.getClass().isAnnotationPresent(NeverCached.class) && isStaticModel();
    }

    /**
     * Returns whether the model is static meaning that is can be cached. Can be set dynamically with {@link #setStaticModel(boolean)}.
     *
     * @return whether the model is static
     * @dxa.publicApi
     */
    default boolean isStaticModel() {
        return false;
    }

    /**
     * Set if the model is static.
     *
     * @param staticModel if the model is static
     * @dxa.publicApi
     */
    default void setStaticModel(boolean staticModel) {
        // default implementation does nothing
    }
}

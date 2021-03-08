package com.sdl.dxa.caching;

/**
 * @deprecated
 * Interface that determines if the implementor model is cacheable and may be cached.
 */
@Deprecated
public interface VolatileModel {

    /**
     * Returns whether the model is cacheable and may be cached.
     *
     * @return whether the model never may be cached
     * This method is subject to be deleted, use isPossibleToCache instead.
     */
    @Deprecated
    default boolean canBeCached() {
        return isPossibleToCache();
    }

    /**
     * Returns whether the model is cacheable and may be cached.
     *
     * @return whether the model never may be cached
     */
    default boolean isPossibleToCache() {
        return !this.getClass().isAnnotationPresent(NeverCached.class) && isStaticModel();
    }

    /**
     * Returns whether the model is static meaning that is can be cached. Can be set dynamically with {@link #setStaticModel(boolean)}.
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

package com.sdl.dxa.api.datamodel.model.util;

/**
 * Implementors of this interface can copy values from another instance to itself.
 */
@FunctionalInterface
public interface CanCopyValues<T> {

    /**
     * Copies values from {@code other} to self. <strong>Should return self!</strong>
     *
     * @param other instance to copy values from
     * @return self populated with values from the given instance
     */
    T copyFrom(T other);
}

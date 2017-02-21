package com.sdl.dxa.api.datamodel.model.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Functional interface to get element from a {@link Collection} and cast it.
 *
 * @param <F> type of an identifier (e.g. key for a {@link Map})
 */
@FunctionalInterface
public interface CanGetAndCast<F> {

    /**
     * Returns and element from the collection and casts it to a given class.
     * Basically calls {@link Map#get(Object)} or {@link List#get(int)} and casts.
     * Throws a {@link ClassCastException} is casting is not successful.
     *
     * @param identifier    key or index of the element
     * @param expectedClass class to cast to
     * @param <T>           a required type
     * @return an element if any, null otherwise
     */
    @JsonIgnore
    default <T> T getAndCast(F identifier, @NotNull Class<T> expectedClass) {
        return expectedClass.cast(getElement(identifier));
    }

    @JsonIgnore
    Object getElement(F identifier);
}

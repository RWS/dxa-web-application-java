package com.sdl.dxa.api.datamodel.model.util;

import java.util.List;

/**
 * Delegates method calls to {@link List}, used for wrappers on top of {@link List} that cannot implement that interface directly.
 * Methods are named intentionally to not collapse with methods name in a {@link List}.
 *
 * @param <T> generic type of a list
 */
@FunctionalInterface
public interface DelegatesToList<T> {

    /**
     * See {@link List#get(int)}.
     */
    default T get(int index) {
        return getValues().get(index);
    }

    /**
     * Returns a wrapped list.
     *
     * @return a wrapped list
     */
    List<T> getValues();

    /**
     * See {@link List#isEmpty()}.
     */
    default boolean empty() {
        return getValues().isEmpty();
    }
}

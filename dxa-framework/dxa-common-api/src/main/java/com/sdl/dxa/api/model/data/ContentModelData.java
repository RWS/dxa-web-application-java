package com.sdl.dxa.api.model.data;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link Map} implemenation to handle DXA polymorphic JSON logic. Is created to be able to address to {@code ContentModelData[]}.
 */
@JsonTypeName
public class ContentModelData extends HashMap<String, Object>
        implements Map<String, Object>, Cloneable, Serializable {

    /**
     * Returns and element from the map and casts it to a given class. Basically calls {@link Map#get(Object)} and casts.
     * Throws a {@link ClassCastException} is casting is not successfull.
     *
     * @param key           key of the element
     * @param expectedClass class to cast to
     * @param <T>           a required type
     * @return en element if any, null otherwise
     */
    public <T> T getAndCast(String key, @NotNull Class<T> expectedClass) {
        return expectedClass.cast(get(key));
    }
}

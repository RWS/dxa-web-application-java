package com.sdl.dxa.api.model.data;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sdl.dxa.api.model.data.util.CanGetAndCast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link Map} implemenation to handle DXA polymorphic JSON logic. Is created to be able to address to {@code ContentModelData[]}.
 */
@JsonTypeName
public class ContentModelData extends HashMap<String, Object>
        implements Map<String, Object>, Cloneable, Serializable, CanGetAndCast<String> {

    @Override
    public Object getElement(String identifier) {
        return get(identifier);
    }
}

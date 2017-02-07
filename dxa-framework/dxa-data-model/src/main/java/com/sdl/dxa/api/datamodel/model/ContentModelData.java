package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sdl.dxa.api.datamodel.model.util.CanGetAndCast;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link Map} implemenation to handle DXA polymorphic JSON logic. Is created to be able to address to {@code ContentModelData[]}.
 */
@JsonTypeName
public class ContentModelData extends HashMap<String, Object>
        implements Map<String, Object>, CanGetAndCast<String> {

    //region Constructors matching super and copy-constructor
    public ContentModelData(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ContentModelData(int initialCapacity) {
        super(initialCapacity);
    }

    public ContentModelData() {
        super();
    }

    public ContentModelData(Map<? extends String, ?> m) {
        super(m);
    }

    public ContentModelData(ContentModelData other) {
    }
    //endregion

    @Override
    public Object getElement(String identifier) {
        return get(identifier);
    }
}

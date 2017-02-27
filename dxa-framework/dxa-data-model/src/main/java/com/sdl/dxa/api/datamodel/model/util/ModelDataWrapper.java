package com.sdl.dxa.api.datamodel.model.util;

import com.sdl.dxa.api.datamodel.model.ContentModelData;

/**
 * Wrapper that build an abstraction on top of what can be considered as {@code content} or {@code metadata} for the current model.
 * While it is basically straight-forward logic, different types of model data may have different data objects to be used as {@code content} or {@code metadata}.
 */
public interface ModelDataWrapper {

    /**
     * Returns what can be considered as {@code content} for the current model.
     */
    default ContentModelData getContent() {
        return new ContentModelData(0);
    }

    /**
     * Returns what can be considered as {@code metadata} for the current model.
     */
    ContentModelData getMetadata();

    /**
     * Returns the wrapped model itself.
     */
    Object getWrappedModel();
}

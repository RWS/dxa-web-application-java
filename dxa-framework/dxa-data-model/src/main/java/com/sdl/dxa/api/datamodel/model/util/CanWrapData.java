package com.sdl.dxa.api.datamodel.model.util;

import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.api.datamodel.model.ViewModelData;

/**
 * Indicates that the implementor can return an instance of {@link ModelDataWrapper} to be used in data providers
 * as an abstraction for {@link ContentModelData} and {@link ViewModelData} that hides a real {@code content} and {@code metadata} objects.
 */
@FunctionalInterface
public interface CanWrapData {

    /**
     * Returns an instance of data wrapper for the current model.
     */
    ModelDataWrapper getDataWrapper();
}

package com.sdl.webapp.common.api.serialization.json;

import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.sdl.webapp.common.api.model.AbstractViewModel;
import com.sdl.webapp.common.views.JsonView;

/**
 * Interface for JSON filter that will be chained as {@link SimpleBeanPropertyFilter}.
 * Used to filter out dynamically properties that are serialized in JSON, for example by {@link JsonView}.
 * All subclass beans of this that are found in current Spring Context will be applied by default to {@link AbstractViewModel}.
 * <strong>The instance should be instantiated in a current Spring context to be picked up.</strong>
 *
 * @dxa.publicApi
 */
@FunctionalInterface
public interface DxaViewModelJsonPropertyFilter {

    /**
     * Return whether current element should be included in JSON serialization.
     *
     * @param writer current element writer
     * @return true to include, false to exclude
     */
    boolean include(PropertyWriter writer);
}

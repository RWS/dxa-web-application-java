package com.sdl.dxa.mvc;

import com.sdl.webapp.common.api.model.MvcData;

/**
 * Resolves a view name based on MVC data and view's type.
 *
 * @dxa.publicApi
 * @since 1.5
 */
@FunctionalInterface
public interface ViewNameResolver {

    /**
     * Resolves a view name from {@link MvcData}, view type and a request.
     *
     * @param mvcData  mvc data with information about the view needed
     * @param viewType type of the view
     * @return view name for the given {@link MvcData}
     */
    String resolveView(MvcData mvcData, String viewType);
}

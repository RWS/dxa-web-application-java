package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModelSet;

import java.util.List;

@FunctionalInterface
public interface RegionBuilder {

    /**
     * Build regions based on a content provider specific source list (DD4T component presentations etc).
     *
     * @param page a {@link com.sdl.webapp.common.api.model.PageModel} object.
     * @param sourceList a {@link java.util.List} object.
     * @param callback a {@link com.sdl.webapp.common.api.content.RegionBuilderCallback} object.
     * @param localization a {@link com.sdl.webapp.common.api.localization.Localization} object.
     * @return a {@link com.sdl.webapp.common.api.model.RegionModelSet} object.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     */
    RegionModelSet buildRegions(PageModel page, List<?> sourceList, RegionBuilderCallback callback,
                                Localization localization) throws ContentProviderException;

}

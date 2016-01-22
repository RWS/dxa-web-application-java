package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModelSet;

import java.util.List;

public interface RegionBuilder {

    /**
     * Build regions based on a content provider specific source list (DD4T component presentations etc).
     */
    RegionModelSet buildRegions(PageModel page, List<?> sourceList, RegionBuilderCallback callback,
                                Localization localization) throws ContentProviderException;

}

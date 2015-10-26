package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.ViewModelRegistry;

import java.util.List;
import java.util.Map;

/**
 * Region Builder
 *
 * @author nic
 */
public interface RegionBuilder {

    /**
     * Build regions based on a content provider specific source list (DD4T component presentations etc)
     *
     * @param page
     * @param sourceList
     * @param callback
     * @param localization
     * @return
     * @throws ContentProviderException
     */
    public RegionModelSet buildRegions(PageModel page,
                                       ConditionalEntityEvaluator conditionalEntityEvaluator,
                                       List<?> sourceList,
                                       RegionBuilderCallback callback,
                                       Localization localization,
                                       ViewModelRegistry viewModelRegistry) throws ContentProviderException;

}

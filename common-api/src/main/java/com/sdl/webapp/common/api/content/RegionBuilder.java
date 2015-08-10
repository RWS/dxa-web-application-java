package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.Page;
import com.sdl.webapp.common.api.model.Region;

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
    public Map<String,Region> buildRegions(Page page,
                                           List<?> sourceList,
                                           RegionBuilderCallback callback,
                                           Localization localization) throws ContentProviderException;

}

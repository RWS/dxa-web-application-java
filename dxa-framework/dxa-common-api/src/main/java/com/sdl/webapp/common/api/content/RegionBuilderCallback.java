package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;

public interface RegionBuilderCallback {

    /**
     * Build entity based on content provider specific source.
     */
    EntityModel buildEntity(Object source, Localization localization) throws ContentProviderException;

    /**
     * Get region name from content provider specific source.
     */
    String getRegionName(Object source) throws ContentProviderException;

    /**
     * Get region MVC data from content provider specific source.
     */
    MvcData getRegionMvcData(Object source) throws ContentProviderException;

}

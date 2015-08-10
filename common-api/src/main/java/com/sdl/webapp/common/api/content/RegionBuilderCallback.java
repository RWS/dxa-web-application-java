package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.MvcData;

/**
 * Region Builder Callback
 *
 * @author nic
 */
public interface RegionBuilderCallback {

    /**
     * Build entity based on content provider specific source
     * @param source
     * @param localization
     * @return entity
     * @throws ContentProviderException
     */
    public Entity buildEntity(Object source, Localization localization) throws ContentProviderException;

    /**
     * Get region name from content provider specific source
     *
     * @param source
     * @return name
     * @throws ContentProviderException
     */
    public String getRegionName(Object source) throws ContentProviderException;

    /**
     * Get region MVC data from content provider specific source
     *
     * @param source
     * @return MVC data
     * @throws ContentProviderException
     */
    public MvcData getRegionMvcData(Object source) throws ContentProviderException;

}

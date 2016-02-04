package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;

/**
 * <p>RegionBuilderCallback interface.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public interface RegionBuilderCallback {

    /**
     * Build entity based on content provider specific source.
     *
     * @param source a {@link java.lang.Object} object.
     * @param localization a {@link com.sdl.webapp.common.api.localization.Localization} object.
     * @return a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     */
    EntityModel buildEntity(Object source, Localization localization) throws ContentProviderException;

    /**
     * Get region name from content provider specific source.
     *
     * @param source a {@link java.lang.Object} object.
     * @return a {@link java.lang.String} object.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     */
    String getRegionName(Object source) throws ContentProviderException;

    /**
     * Get region MVC data from content provider specific source.
     *
     * @param source a {@link java.lang.Object} object.
     * @return a {@link com.sdl.webapp.common.api.model.MvcData} object.
     * @throws com.sdl.webapp.common.api.content.ContentProviderException if any.
     */
    MvcData getRegionMvcData(Object source) throws ContentProviderException;

}

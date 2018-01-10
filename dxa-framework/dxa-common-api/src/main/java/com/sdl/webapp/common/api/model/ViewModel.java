package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.api.localization.Localization;

import java.util.Map;

/**
 * Basic DXA View Model. Everything that is a part of entity/region/page models in DXA is a ViewModel.
 *
 * @dxa.publicApi
 */
public interface ViewModel {

    /**
     * Returns the MVC information for this ViewModel, containing data of view name, view area, and other.
     *
     * @return a {@link MvcData} object
     */
    MvcData getMvcData();

    /**
     * Sets MVC information for this ViewModel, containing data of view name, view area, and other.
     *
     * @param mvcData MVC information for this ViewModel
     */
    void setMvcData(MvcData mvcData);

    /**
     * Returns XPM metadata for the current {@link ViewModel}.
     *
     * @return current XPM metadata
     */
    Map<String, Object> getXpmMetadata();

    /**
     * Gets the XPM markup to be output by the Html.DxaRegionMarkup() method.
     *
     * @param localization the context localization
     * @return the XPM markup
     */
    String getXpmMarkup(Localization localization);

    /**
     * Returns a string with all CSS classes for this view model.
     *
     * @return CSS classes
     */
    String getHtmlClasses();

    /**
     * Sets a string with all CSS classes for this view model.
     *
     * @param classes a string with all CSS classes
     */
    void setHtmlClasses(String classes);

    /**
     * Returns the extension data (additional properties which can be used by custom Model Builders, Controllers and/or Views).
     *
     * @return the extension data
     */
    Map<String, Object> getExtensionData();

    /**
     * Adds an entry to an extension data. Extension data is used to provide some extra functionality (extensions) with some additional data.
     *
     * @param key   key of the entry
     * @param value value of the entry
     */
    void addExtensionData(String key, Object value);
}

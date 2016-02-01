package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.api.localization.Localization;

import java.util.Map;

/**
 * Superinterface for view model interfaces and classes.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public interface ViewModel {

    /**
     * <p>getMvcData.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.MvcData} object.
     */
    MvcData getMvcData();

    /**
     * <p>getXpmMetadata.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<String, String> getXpmMetadata();

    // TODO: Is this the right way forward? Is it not better to use markup decorators for this?

    /**
     * Gets the XPM markup to be output by the Html.DxaRegionMarkup() method.
     *
     * @param localization the context localization
     * @return the XPM markup
     */
    String getXpmMarkup(Localization localization);

    /**
     * <p>getHtmlClasses.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getHtmlClasses();

    /**
     * <p>setHtmlClasses.</p>
     *
     * @param s a {@link java.lang.String} object.
     */
    void setHtmlClasses(String s);

}

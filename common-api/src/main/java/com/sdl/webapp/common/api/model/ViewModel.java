package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.api.localization.Localization;

import java.util.Map;

/**
 * Superinterface for view model interfaces and classes.
 */
public interface ViewModel {

    MvcData getMvcData();

    Map<String, String> getXpmMetadata();

    // TODO: Is this the right way forward? Is it not better to use markup decorators for this?
    String getXpmMarkup(Localization localization);

    String getHtmlClasses();

}

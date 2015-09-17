package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.api.localization.Localization;

import java.util.Map;

/**
 * Superinterface for view model interfaces and classes.
 */
public interface ViewModel {

    MvcData getMvcData();
    
    Map<String, String> getXpmMetadata();

    String getXpmMarkup(Localization localization);

    String getHtmlClasses();
    
}

package com.sdl.webapp.common.api.model;

import java.util.Map;

/**
 * Superinterface for view model interfaces and classes.
 */
public interface ViewModel {

    MvcData getMvcData();

    Map<String, String> getXpmMetadata();

    String getHtmlClasses();

}

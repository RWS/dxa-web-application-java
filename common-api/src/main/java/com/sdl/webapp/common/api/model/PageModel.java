package com.sdl.webapp.common.api.model;

import java.util.Map;

/**
 * Page model.
 */
public interface PageModel extends ViewModel {

    String getId();

    String getName();

    String getTitle();

    Map<String, String> getMeta();

    Map<String, PageModel> getIncludes();

    RegionModelSet getRegions();
}

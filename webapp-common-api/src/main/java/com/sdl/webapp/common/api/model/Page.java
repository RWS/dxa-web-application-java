package com.sdl.webapp.common.api.model;

import java.util.Map;

public interface Page extends ViewModel {

    String getId();

    String getName();

    String getTitle();

    Map<String, String> getMeta();

    Map<String, Page> getIncludes();

    Map<String, Region> getRegions();

    Map<String, String> getPageData();
}

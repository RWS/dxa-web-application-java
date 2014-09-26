package com.sdl.tridion.referenceimpl.common.model;

import java.util.Map;

public interface Page extends ViewModel {

    String getId();

    String getTitle();

    Map<String, Page> getIncludes();

    Map<String, Region> getRegions();

    Map<String, Entity> getEntities();

    Map<String, String> getPageData();
}

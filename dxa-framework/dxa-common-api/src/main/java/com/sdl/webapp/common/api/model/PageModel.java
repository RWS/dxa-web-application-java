package com.sdl.webapp.common.api.model;

import java.util.Map;

public interface PageModel extends ViewModel {

    String getId();

    void setId(String Id);

    String getName();

    void setName(String name);

    String getTitle();

    void setTitle(String name);

    Map<String, String> getMeta();

    void setMeta(Map<String, String> pageMeta);

    RegionModelSet getRegions();

    void setRegions(RegionModelSet regions);

    boolean containsRegion(String regionName);

    void setMvcData(MvcData pageMvcData);

    void setXpmMetadata(Map<String, String> xpmMetaData);
}

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

    RegionModelSet getRegions();

    void setId(String Id);

    void setName(String name);

    void setTitle(String name);

    void setRegions(RegionModelSet regions);

    void setMeta(Map<String, String> pageMeta);

    void setMvcData(MvcData pageMvcData);

    void setXpmMetadata(Map<String, String> xpmMetaData);

    void setHtmlClasses(String s);
}

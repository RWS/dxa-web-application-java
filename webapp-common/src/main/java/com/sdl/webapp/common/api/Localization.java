package com.sdl.webapp.common.api;

import java.util.List;

public interface Localization {

    String getId();

    String getPath();

    boolean isStaticContent(String url);

    String getResource(String key);

    List<String> getIncludes(String pageTypeId);
}

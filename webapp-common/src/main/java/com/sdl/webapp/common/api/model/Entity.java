package com.sdl.webapp.common.api.model;

import java.util.Map;

public interface Entity extends ViewModel {

    // TODO: This does not belong here
    String CORE_VOCABULARY = "http://www.sdl.com/web/schemas/core";

    String getId();

    Map<String, String> getPropertyData();

    Map<String, String> getEntityData();
}

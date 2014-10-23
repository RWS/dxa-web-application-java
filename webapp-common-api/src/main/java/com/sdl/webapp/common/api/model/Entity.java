package com.sdl.webapp.common.api.model;

import java.util.Map;

public interface Entity extends ViewModel {

    String getId();

    Map<String, String> getPropertyData();

    Map<String, String> getEntityData();
}

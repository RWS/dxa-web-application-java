package com.sdl.webapp.common.api.model;

import java.util.Map;

/**
 * Entity model.
 */
public interface Entity extends ViewModel {

    String getId();

    Map<String, String> getEntityData();

    Map<String, String> getPropertyData();
}

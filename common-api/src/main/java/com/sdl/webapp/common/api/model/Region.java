package com.sdl.webapp.common.api.model;

import java.util.Map;

/**
 * Region model.
 */
public interface Region extends ViewModel {

    String getName();

    Map<String, Entity> getEntities();

    Map<String, String> getRegionData();

    Entity getEntity(String entityId);
}

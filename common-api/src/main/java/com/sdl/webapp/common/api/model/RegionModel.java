package com.sdl.webapp.common.api.model;

import java.util.Map;

/**
 * Region model.
 */
public interface RegionModel extends ViewModel {

    String getName();

    Map<String, EntityModel> getEntities();

    EntityModel getEntity(String entityId);
}

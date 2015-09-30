package com.sdl.webapp.common.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Region model.
 */
public interface RegionModel extends ViewModel {

    String getName();

    List<EntityModel> getEntities();

    EntityModel getEntity(String entityId);

    RegionModelSet getRegions();

    void setMvcData(MvcData value);
}

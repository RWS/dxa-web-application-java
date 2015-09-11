package com.sdl.webapp.common.api.model;

import java.util.ArrayList;
import java.util.Map;

/**
 * Region model.
 */
public interface RegionModel extends ViewModel {

    String getName();

    ArrayList<EntityModel> getEntities();

    EntityModel getEntity(String entityId);
    
    RegionModelSet getRegions();

    void setMvcData(MvcData value);
}

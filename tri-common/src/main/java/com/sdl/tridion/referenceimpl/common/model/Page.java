package com.sdl.tridion.referenceimpl.common.model;

import java.util.List;
import java.util.Map;

public interface Page {

    String getId();

    String getViewName();

    Map<String, Region> getRegions();

    Region getRegion(String regionViewName);

    Map<String, Entity> getEntities();

    Entity getEntity(String entityId);
}

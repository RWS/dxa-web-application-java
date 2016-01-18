package com.sdl.webapp.common.api.model;

import java.util.Set;

public interface RegionModelSet extends Set<RegionModel> {

    RegionModel get(String name);

    Set<RegionModel> get(Class<? extends RegionModel> clazz);

    boolean containsName(String name);

    boolean containsClass(Class<? extends RegionModel> clazz);
}

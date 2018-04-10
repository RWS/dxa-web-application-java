package com.sdl.webapp.common.api.model;

import java.util.Set;

/**
 * <p>RegionModelSet interface.</p>
 * @dxa.publicApi
 */
public interface RegionModelSet extends Set<RegionModel> {

    RegionModel get(String name);

    <T extends RegionModel> Set<T> get(Class<T> clazz);

    boolean containsName(String name);

    boolean containsClass(Class<? extends RegionModel> clazz);
}

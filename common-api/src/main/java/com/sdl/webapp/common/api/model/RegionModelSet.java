package com.sdl.webapp.common.api.model;

import java.util.Set;

public interface RegionModelSet extends Set<RegionModel> {

	RegionModel get(String name);

	Boolean containsKey(String name);	
}

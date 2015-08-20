package com.sdl.webapp.common.api.model;

import java.util.List;

public interface RegionModelSet extends List<RegionModel> {

	RegionModel get(String name);

	Boolean containsKey(String name);	
}

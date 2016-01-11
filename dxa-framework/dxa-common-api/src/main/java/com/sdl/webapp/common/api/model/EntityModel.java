package com.sdl.webapp.common.api.model;

import java.util.Map;

public interface EntityModel extends ViewModel {

    String getId();

    Map<String, String> getXpmPropertyMetadata();

}

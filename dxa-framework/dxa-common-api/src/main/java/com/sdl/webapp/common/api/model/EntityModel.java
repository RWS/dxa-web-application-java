package com.sdl.webapp.common.api.model;

import java.util.Map;

/**
 * EntityModel interface represents an entity.
 */
public interface EntityModel extends ViewModel {

    String getId();

    Map<String, String> getXpmPropertyMetadata();
}

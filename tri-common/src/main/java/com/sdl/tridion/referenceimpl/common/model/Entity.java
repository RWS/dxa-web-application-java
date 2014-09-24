package com.sdl.tridion.referenceimpl.common.model;

import java.util.Map;

public interface Entity extends ViewModel {

    String CORE_VOCABULARY = "http://www.sdl.com/web/schemas/core";

    String getId();

    Map<String, String> getPropertyData();

    Map<String, String> getEntityData();
}

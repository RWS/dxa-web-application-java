package com.sdl.webapp.common.api.contextengine;

import java.util.Map;

public interface ContextClaimsProvider {

    Map<String, Object> getContextClaims(String aspectName);

    String getDeviceFamily();

}
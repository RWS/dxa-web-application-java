package com.sdl.webapp.common.api.contextengine;

import com.sdl.webapp.common.exceptions.DxaException;

import java.util.Map;

public interface ContextClaimsProvider {

    Map<String, Object> getContextClaims(String aspectName) throws DxaException;

    /**
     * TSI-789: this functionality overlaps with "Context Expressions".
     */
    String getDeviceFamily();

}
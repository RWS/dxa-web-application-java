package com.sdl.webapp.common.api.contextengine;

import com.sdl.webapp.common.exceptions.DxaException;

import java.util.Map;

/**
 * <p>ContextClaimsProvider interface.</p>
 */
public interface ContextClaimsProvider {

    /**
     * <p>getContextClaims.</p>
     *
     * @param aspectName a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    Map<String, Object> getContextClaims(String aspectName) throws DxaException;

    /**
     * TSI-789: this functionality overlaps with "Context Expressions".
     *
     * @return a {@link java.lang.String} object.
     */
    String getDeviceFamily();
}

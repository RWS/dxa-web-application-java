package com.sdl.webapp.common.api.contextengine;

import com.sdl.webapp.common.exceptions.DxaException;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Context Claims Provider is an interface implementors of which provide you with context claims data.
 */
public interface ContextClaimsProvider {

    /**
     * Returns a map of context claims. Can accept aspect to give you only some specific claims.
     *
     * @param aspectName a name of aspect, e.g. 'browser'
     * @return context claims collection
     * @throws com.sdl.webapp.common.exceptions.DxaException is error occurred during resolving claims
     */
    Map<String, Object> getContextClaims(String aspectName) throws DxaException;

    /**
     * Return a name of device family. Typically this should happen in a {@link ContextEngine#getDeviceFamily()},
     * but you may want to implement this if you want some custom logic.
     *
     * @return a name of device family, or typically <code>null</code>
     */
    @Nullable
    String getDeviceFamily();
}

package com.sdl.webapp.common.api.contextengine;

import com.sdl.webapp.common.exceptions.DxaException;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Context Claims Provider is an interface implementors of which provide you with context claims data.
 *
 * @dxa.publicApi
 */
public interface ContextClaimsProvider {

    /**
     * Returns a map of context claims. Can accept aspect to give you only some specific claims.
     * <p><strong>NB!</strong> If you are not using subclasses of {@link ContextClaims}, then your are not fully type-safe because
     * context claim is of type {@link Object} and the real class is defined in runtime. It is wise then to use
     * {@link ContextClaims#castClaim(Object, Class)} to be type-safe and not get a {@link ClassCastException}.</p>
     *
     * @param aspectName a name of aspect, e.g. 'browser'
     * @return context claims collection
     * @throws com.sdl.webapp.common.exceptions.DxaException is error occurred during resolving claims
     */
    //todo dxa2 change return type to a Map wrapper which wouldn't allow to request Object but only generic-type?
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

package com.sdl.webapp.tridion;

import com.sdl.webapp.common.api.localization.LocalizationResolverException;

/**
 * <p>PublicationMappingNotFoundException class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class PublicationMappingNotFoundException extends LocalizationResolverException {

    /**
     * <p>Constructor for PublicationMappingNotFoundException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public PublicationMappingNotFoundException(String message) {
        super(message);
    }
}

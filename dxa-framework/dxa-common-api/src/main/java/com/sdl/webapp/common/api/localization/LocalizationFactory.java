package com.sdl.webapp.common.api.localization;

/**
 * Localization factory.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public interface LocalizationFactory {

    /**
     * Creates a localization with the specified ID and path.
     *
     * @param id   The localization ID.
     * @param path The localization path.
     * @return A {@code Localization} with the specified ID and path.
     * @throws com.sdl.webapp.common.api.localization.LocalizationFactoryException If an error occurs so that the localization cannot be created.
     */
    Localization createLocalization(String id, String path) throws LocalizationFactoryException;
}

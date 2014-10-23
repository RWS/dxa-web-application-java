package com.sdl.webapp.common.api.localization;

/**
 * Localization factory.
 */
public interface LocalizationFactory {

    /**
     * Creates a localization with the specified ID and path.
     *
     * @param id The localization ID.
     * @param path The localization path.
     * @return A {@code Localization} with the specified ID and path.
     * @throws LocalizationFactoryException If an error occurs so that the localization cannot be created.
     */
    Localization createLocalization(String id, String path) throws LocalizationFactoryException;
}

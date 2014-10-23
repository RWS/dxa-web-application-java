package com.sdl.webapp.common.api.localization;

public interface LocalizationFactory {

    Localization createLocalization(String id, String path) throws LocalizationFactoryException;
}

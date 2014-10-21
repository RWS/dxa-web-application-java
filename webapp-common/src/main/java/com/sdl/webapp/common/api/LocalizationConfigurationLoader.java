package com.sdl.webapp.common.api;

import com.sdl.webapp.common.impl.LocalizationImpl;

public interface LocalizationConfigurationLoader {

    void loadConfiguration(LocalizationImpl localization) throws LocalizationConfigurationLoaderException;
}

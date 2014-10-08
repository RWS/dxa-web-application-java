package com.sdl.tridion.referenceimpl.common.config;

import java.io.IOException;

public interface LocalizationProvider {

    Localization getLocalization(String url) throws IOException;
}

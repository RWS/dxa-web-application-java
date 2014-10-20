package com.sdl.webapp.common.api;

public interface WebRequestContext {

    Localization getLocalization();

    void setLocalization(Localization localization);

    ScreenWidth getScreenWidth();

    int getDisplayWidth();

    double getPixelRatio();

    int getMaxMediaWidth();
}

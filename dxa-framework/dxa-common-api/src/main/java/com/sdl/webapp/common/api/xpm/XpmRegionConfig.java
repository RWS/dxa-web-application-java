package com.sdl.webapp.common.api.xpm;

import com.sdl.webapp.common.api.localization.Localization;

@FunctionalInterface
public interface XpmRegionConfig {

    XpmRegion getXpmRegion(String regionName, Localization localization);
}

package com.sdl.webapp.common.api.xpm;

import com.sdl.webapp.common.api.localization.Localization;

/**
 * Created by Administrator on 17/09/2015.
 */
public interface XpmRegionConfig {
    XpmRegion getXpmRegion(String regionName, Localization localization);
}

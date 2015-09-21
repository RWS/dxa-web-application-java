package com.sdl.webapp.common.api.xpm;

import java.util.List;

/**
 * Created by Administrator on 17/09/2015.
 */
public interface XpmRegion {
    String getRegionName();

    void setRegionName(String regionName);

    List<ComponentType> getComponentTypes();

    void setComponentTypes(List<ComponentType> componentTypes);

}

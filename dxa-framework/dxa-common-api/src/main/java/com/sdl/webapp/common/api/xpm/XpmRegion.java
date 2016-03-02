package com.sdl.webapp.common.api.xpm;

import java.util.List;

public interface XpmRegion {
    /**
     * <p>getRegionName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getRegionName();

    /**
     * <p>setRegionName.</p>
     *
     * @param regionName a {@link java.lang.String} object.
     */
    void setRegionName(String regionName);

    /**
     * <p>getComponentTypes.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<ComponentType> getComponentTypes();

    /**
     * <p>setComponentTypes.</p>
     *
     * @param componentTypes a {@link java.util.List} object.
     */
    void setComponentTypes(List<ComponentType> componentTypes);

}

package com.sdl.webapp.common.api.navigation;

import lombok.Data;

/**
 * Navigation filter that holds information about the requested navigation in {@link OnDemandNavigationProvider}.
 */
@Data
public class NavigationFilter {

    public static final NavigationFilter DEFAULT = new NavigationFilter();

    private boolean withAncestors;

    private int descendantLevels = 1;
}

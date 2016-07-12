package com.sdl.webapp.common.impl.taglib.dxa;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.localization.Localization;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

/**
 * <p>Abstract AbstractGoogleMapTag class.</p>
 */
public abstract class AbstractGoogleMapTag extends HtmlNodeTag {

    private static final String CONFIG_MAPS_API_KEY = "core.mapsApiKey";
    private static final String DUMMY_API_KEY = "xxx";

    private static final int DEFAULT_MAP_WIDTH = 311;
    private static final int DEFAULT_MAP_HEIGHT = 160;

    @Setter
    private double latitude;

    @Setter
    private double longitude;

    @Setter
    @Getter
    private String markerName;

    @Setter
    @Getter
    private int mapWidth = DEFAULT_MAP_WIDTH;

    @Setter
    @Getter
    private int mapHeight = DEFAULT_MAP_HEIGHT;

    /**
     * <p>getMapsApiKey.</p>
     *
     * @param localization a {@link com.sdl.webapp.common.api.localization.Localization} object.
     * @return a {@link java.lang.String} object.
     */
    protected static String getMapsApiKey(Localization localization) {
        final String mapsApiKey = localization.getConfiguration(CONFIG_MAPS_API_KEY);
        return mapsApiKey.equals(DUMMY_API_KEY) ? "" : mapsApiKey;
    }

    /**
     * <p>getLatString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected String getLatString() {
        return String.format(Locale.US, "%f", latitude);
    }

    /**
     * <p>getLonString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected String getLonString() {
        return String.format(Locale.US, "%f", longitude);
    }
}

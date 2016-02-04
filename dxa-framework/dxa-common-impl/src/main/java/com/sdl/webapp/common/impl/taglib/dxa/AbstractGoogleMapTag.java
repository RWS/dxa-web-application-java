package com.sdl.webapp.common.impl.taglib.dxa;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.localization.Localization;

import java.util.Locale;

/**
 * <p>Abstract AbstractGoogleMapTag class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public abstract class AbstractGoogleMapTag extends HtmlNodeTag {

    private static final String CONFIG_MAPS_API_KEY = "core.mapsApiKey";
    private static final String DUMMY_API_KEY = "xxx";

    private static final int DEFAULT_MAP_WIDTH = 311;
    private static final int DEFAULT_MAP_HEIGHT = 160;

    private double latitude;
    private double longitude;
    private String markerName;
    private int mapWidth = DEFAULT_MAP_WIDTH;
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
     * <p>Setter for the field <code>latitude</code>.</p>
     *
     * @param latitude a double.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * <p>Setter for the field <code>longitude</code>.</p>
     *
     * @param longitude a double.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * <p>Getter for the field <code>markerName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMarkerName() {
        return Strings.nullToEmpty(markerName);
    }

    /**
     * <p>Setter for the field <code>markerName</code>.</p>
     *
     * @param markerName a {@link java.lang.String} object.
     */
    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    /**
     * <p>Getter for the field <code>mapWidth</code>.</p>
     *
     * @return a int.
     */
    public int getMapWidth() {
        return mapWidth;
    }

    /**
     * <p>Setter for the field <code>mapWidth</code>.</p>
     *
     * @param mapWidth a int.
     */
    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    /**
     * <p>Getter for the field <code>mapHeight</code>.</p>
     *
     * @return a int.
     */
    public int getMapHeight() {
        return mapHeight;
    }

    /**
     * <p>Setter for the field <code>mapHeight</code>.</p>
     *
     * @param mapHeight a int.
     */
    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
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

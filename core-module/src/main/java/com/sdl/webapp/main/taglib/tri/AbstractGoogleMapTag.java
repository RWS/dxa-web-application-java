package com.sdl.webapp.main.taglib.tri;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.localization.Localization;

import java.util.Locale;

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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMarkerName() {
        return Strings.nullToEmpty(markerName);
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    protected String getLatString() {
        return String.format(Locale.US, "%f", latitude);
    }

    protected String getLonString() {
        return String.format(Locale.US, "%f", longitude);
    }

    protected String getMapsApiKey(Localization localization) {
        final String mapsApiKey = localization.getConfiguration(CONFIG_MAPS_API_KEY);
        return mapsApiKey.equals(DUMMY_API_KEY) ? "" : mapsApiKey;
    }
}

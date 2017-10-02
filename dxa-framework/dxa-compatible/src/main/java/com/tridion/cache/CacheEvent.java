package com.tridion.cache;

import java.io.Serializable;

/**
 * Simple bean type class for representing cache events. A cache event affects a certain object in a certain
 * region and has a certain type (flush or invalidate).
 */
public class CacheEvent implements Serializable {

    public static final int FLUSH = 0;

    public static final int INVALIDATE = 1;

    private static final String[] typeNames = new String[]{"Flush", "Invalidate"};

    private static final long serialVersionUID = 1252887747292650827L;

    private String regionPath;

    private Serializable key;

    private int eventType;

    /**
     * Constructs a new cache event.
     *
     * @param regionPath The path of the region.
     * @param key        The object key.
     * @param eventType  The type of the event.
     */
    public CacheEvent(String regionPath, Serializable key, int eventType) {
        this.regionPath = regionPath;
        this.key = key;
        this.eventType = eventType;
    }

    /**
     * Gets the region path for this event.
     *
     * @return The region path.
     */
    public String getRegionPath() {
        return regionPath;
    }

    /**
     * Gets the object key for this event.
     *
     * @return The object key.
     */
    public Serializable getKey() {
        return key;
    }

    /**
     * Gets the type of this event.
     *
     * @return The event type.
     */
    public int getType() {
        return eventType;
    }

    /**
     * Overridden to show all event information (type, region path and key).
     *
     * @return String representation of this event.
     */
    public String toString() {
        return "[CacheEvent eventType=" + typeNames[this.eventType] + " regionPath=" + this.regionPath + " key="
                + this.key + "]";
    }
}

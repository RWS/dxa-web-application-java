package com.tridion.cache;

import java.io.Serializable;

public class CacheEvent implements Serializable {

    public static final int FLUSH = 0;
    public static final int INVALIDATE = 1;
    private static final String[] TYPE_NAMES = {"Flush", "Invalidate"};
    private final String regionPath;
    private final Serializable key;
    private final int eventType;

    public CacheEvent(String regionPath, Serializable key, int eventType) {
        this.regionPath = regionPath;
        this.key = key;
        this.eventType = eventType;
    }

    public String getRegionPath() {
        return this.regionPath;
    }

    public Serializable getKey() {
        return this.key;
    }

    public int getType() {
        return this.eventType;
    }

    public String toString() {
        return "[CacheEvent eventType=" + TYPE_NAMES[this.eventType] + " regionPath=" + this.regionPath + " key=" + this.key + "]";
    }
}

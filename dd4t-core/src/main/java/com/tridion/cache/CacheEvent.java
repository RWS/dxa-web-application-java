/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

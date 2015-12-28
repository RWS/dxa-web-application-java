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

package org.dd4t.core.caching.impl;

import org.dd4t.core.caching.CacheElement;

/**
 * Wrapper around a 'payload' object that represents the actual object in the
 * cache. The payload is wrapped inside this cache element object, so we can
 * differentiate between null and not-null payloads. The cache element can also
 * contain empty (null) payloads.
 *
 * @author Mihai Cadariu
 */
public class CacheElementImpl<T> implements CacheElement<T> {
    private boolean isExpired;
    private T payload;
    private String dependentKey;

    public CacheElementImpl (T payload) {
        this(payload, false);
    }

    public CacheElementImpl (T payload, boolean isExpired) {
        this.payload = payload;
        this.isExpired = isExpired;
    }

    @Override
    public boolean isExpired () {
        return isExpired;
    }

    @Override
    public void setExpired (boolean expired) {
        this.isExpired = expired;
    }

    @Override
    public T getPayload () {
        return payload;
    }

    @Override
    public void setPayload (T payload) {
        this.payload = payload;
    }

    @Override
    public String getDependentKey () {
        return dependentKey;
    }

    @Override
    public void setDependentKey (String dependentKey) {
        this.dependentKey = dependentKey;
    }
}

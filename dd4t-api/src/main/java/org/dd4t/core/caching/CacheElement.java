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

package org.dd4t.core.caching;

/**
 * Wrapper around a 'payload' object that represents the actual object in the
 * cache. The payload is wrapped inside this cache element object, so we can
 * differentiate between null and not-null payloads. The cache element can also
 * contain empty (null) payloads.
 * 
 * @author Mihai Cadariu
 */
public interface CacheElement<T> {
	public T getPayload();

	public void setPayload(T payload);

	public boolean isExpired();

	public void setExpired(boolean update);

	public String getDependentKey();

	void setDependentKey(String dependentKey);
}

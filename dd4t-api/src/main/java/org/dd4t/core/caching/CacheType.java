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
 * Enumeration of cache types. These are regions in the cache that are differentiated by their key. Each key has a prefix
 * that corresponds to each of the enum values defined in this structure.
 *
 * @author Mihai Cadariu
 */
public enum CacheType {

	BINARY_CONTENT("BC"),
	BINARY_META("BM"),
	PAGE_CONTENT("PC"),
	PAGE_META("PM"),
	COMPONENT_LINK("CL"),
	COMPONENT_LINK_PAGE("CLP"),
	TAXONOMY("T"),
	RELATED_KEYWORD("RK"),
	RELATED_KEYWORD_BY_SCHEMA("RKS"),
	COMPONENT_CONTENT("CC"),
	DISCOVER_PUBLICATION_URL("DP"),
	DISCOVER_IMAGES_URL("DI"),
	PAGE_EXISTS("PE"),
	PAGE_LIST("PL"),
	SEARCH_CUSTOM_META("SCM"),
	COMPONENTS_BY_SCHEMA("CBS"),
	COMPONENTS_BY_SCHEMA_IN_KEYWORD("CBSK"),
	PUBLICATION_META("PUB");

	private String id;

	/**
	 * Initialization constructor
	 *
	 * @param id String representing the value of the enumeration value
	 */
	CacheType(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return id;
	}
}
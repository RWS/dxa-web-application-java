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

	public String toString() {
		return id;
	}
}
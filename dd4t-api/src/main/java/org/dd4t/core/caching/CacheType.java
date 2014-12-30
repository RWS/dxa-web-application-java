package org.dd4t.core.caching;


/**
 * Enumeration of cache types. These are regions in the cache that are differentiated by their key. Each key has a prefix
 * that corresponds to each of the enum values defined in this structure.
 *
 * @author Mihai Cadariu
 */
public enum CacheType {

	BinaryContent("BC"),
	BinaryMeta("BM"),
	PageContent("PC"),
	PageMeta("PM"),
	ComponentLink("CL"),
	ComponentLinkPage("CLP"),
	Taxonomy("T"),
	RelatedKeyword("RK"),
	RelatedKeywordBySchema("RKS"),
	ComponentContent("CC"),
	DiscoverPublicationURL("DP"),
	DiscoverImagesURL("DI"),
	PageExists("PE"),
	PageList("PL"),
	SearchCustomMeta("SCM"),
	ComponentsBySchema("CBS"),
	ComponentsBySchemaInKeyword("CBSK");

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
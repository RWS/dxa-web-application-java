package org.dd4t.caching.impl;

import org.dd4t.caching.CacheDependency;

/**
 * Basic implementation class for a CacheDependency. Supports creation through constructor as well as setters.
 * 
 * @author Rogier Oudshoorn
 *
 */
public class CacheDependencyImpl implements CacheDependency{

	private int publicationId;
	
	private int itemId;

	public CacheDependencyImpl(int dependingPublicationId, int dependingItemId) {
		this.publicationId = dependingPublicationId;
		this.itemId = dependingItemId;	
	}
	
	@Override
	public int getPublicationId() {
		return publicationId;
	}

	@Override
	public int getItemId() {
		return itemId;
	}

	void setPublicationId(int dependingPublicationId){
		publicationId = dependingPublicationId;
	}
	
	void setItemId(int dependingItemId){
		this.itemId = dependingItemId;
	}
	
}

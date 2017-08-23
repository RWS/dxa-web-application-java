package org.dd4t.caching;

/**
 * Interface determines the necessary data to act as a cache dependency
 * 
 * @author Rogier Oudshoorn
 *
 */
public interface CacheDependency {
	public int getPublicationId();
	
	public int getItemId();
}

/**  
 *  Copyright 2011 Capgemini & SDL
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dd4t.contentmodel.impl;

import org.dd4t.contentmodel.PublishedItem;
import org.dd4t.core.caching.Cachable;

import com.tridion.storage.ItemMeta;
import com.tridion.util.ObjectSizeProvider;
import org.dd4t.contentmodel.impl.BaseRepositoryLocalItem;;

/**
 * Base class for all items which can be published
 * 
 * @author bjornl
 * 
 */
public abstract class BasePublishedItem extends BaseRepositoryLocalItem implements PublishedItem, Cachable, ObjectSizeProvider {

	private String source;
	private ItemMeta nativeMetadata;
	private int objectSize;

	// cache settings
	protected String cacheKey;
	protected String cacheRealm;

	
	/**
	 * Get the published source
	 * 
	 * @return the source as a string
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Set the source
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Set the native tridion metadata
	 */
	public void setNativeMetadata(ItemMeta metadata) {
		this.nativeMetadata = metadata;
	}

	/**
	 * Get the native tridion metadata
	 * 
	 * @return the metadata
	 */
	public ItemMeta getNativeMetadata() {
		return nativeMetadata;
	}
	
	public void notifyCached(String key, String realm) {
		this.cacheKey = key;
		this.cacheRealm = realm;
	}

	public String getCacheRealm() {
		if(this.cacheRealm == null){
			this.cacheRealm = getPublication().getId();
		}
		return this.cacheRealm;
	}

	public String getCacheKey() {
		if(this.cacheKey == null){
			this.cacheKey = getId();
		}
		return this.cacheKey;
	}	
	
	/**
	 * Sets source size for item
	 * 
	 * @param value
	 */
	public void setSourceSize(int value){
		objectSize = value / 2;
	}
	
	/**
	 * Determines objectsize when item is cached
	 * 
	 * @return integer defining cache size
	 */
	public int getObjectSize(){
		if(source != null){
			return source.length() * 3 / 2;
		}
			
		return objectSize + 1;
	}
}

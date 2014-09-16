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
package org.dd4t.core.filters;

import org.dd4t.contentmodel.Item;
import org.dd4t.contentmodel.exceptions.NotAuthenticatedException;
import org.dd4t.contentmodel.exceptions.NotAuthorizedException;
import org.dd4t.core.request.RequestContext;

/**
 * Interface for filters.
 * 
 * @author bjornl
 * 
 */
public interface Filter {

	/**
	 * Execute the filter
	 * 
	 * @param item
	 * @param context
	 * @throws FilterException
	 * @throws NotAuthorizedException
	 * @throws NotAuthenticatedException 
	 */
	public void doFilter(Item item, RequestContext context)
			throws FilterException, NotAuthorizedException, NotAuthenticatedException;

	/**
	 * Returns if the result of the filter is allowed to be cached.
	 * 
	 * @return true if the result of the filter is allowed to be cached.
	 */
	public boolean getCachingAllowed();

	/**
	 * Set if the result of the filter is allowed to be cached.
	 */
	public void setCachingAllowed(boolean cachingAllowed);
}
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
package org.dd4t.core.factories;

import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.NotAuthenticatedException;
import org.dd4t.contentmodel.exceptions.NotAuthorizedException;
import org.dd4t.core.request.RequestContext;

public interface PageFactory extends Factory {

	/**
	 * Get a page by its URI. No security available; the method will fail if a
	 * SecurityFilter is configured on the factory.
	 * 
	 * @param uri
	 *            of the page
	 * @return
	 * @throws ItemNotFoundException
	 */
	public Page getPage(String uri) throws ItemNotFoundException;

	/**
	 * Get a page by its URI. The request context is used by the security filter
	 * (if there is one).
	 * 
	 * @param uri
	 *            of the page
	 * @param context
	 *            (normally wrapped around the HttpServletRequest)
	 * @return
	 * @throws ItemNotFoundException
	 * @throws NotAuthorizedException
	 * @throws NotAuthenticatedException 
	 */
	public Page getPage(String uri, RequestContext context)
			throws ItemNotFoundException, NotAuthorizedException, NotAuthenticatedException;

	/**
	 * Find page by its URL. The url and publication id are specified. No
	 * security available; the method will fail if a SecurityFilter is
	 * configured on the factory.
	 * 
	 * @return
	 * @throws ItemNotFoundException
	 */
	public Page findPageByUrl(String url, int publicationId)
			throws ItemNotFoundException;

	/**
	 * Find page by its URL. The url and publication id are specified, the
	 * RequestContext is only used for security.
	 * 
	 * @param context
	 * @return
	 * @throws ItemNotFoundException
	 * @throws NotAuthorizedException
	 * @throws NotAuthenticatedException 
	 */
	public Page findPageByUrl(String url, int publicationId,
			RequestContext context) throws ItemNotFoundException,
			NotAuthorizedException, NotAuthenticatedException;
}

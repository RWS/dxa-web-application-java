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

import java.text.ParseException;

import org.dd4t.contentmodel.Binary;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.NotAuthenticatedException;
import org.dd4t.contentmodel.exceptions.NotAuthorizedException;
import org.dd4t.core.request.RequestContext;

import com.tridion.broker.StorageException;
/**
 * Interface for factories that return binary items (e.g. images, office documents).
 * @author Quirijn Slings
 *
 */
public interface BinaryFactory extends Factory {
	/**
	 * Get a binary by the uri. The request context is used by the security filter (if there is one).
	 * @param uri
	 * @param context
	 * @return
	 * @throws ItemNotFoundException
	 * @throws NotAuthorizedException
	 * @throws ParseException 
	 * @throws StorageException 
	 * @throws NotAuthenticatedException 
	 */
	public Binary getBinary(String uri, RequestContext context) throws ItemNotFoundException, NotAuthorizedException, StorageException, ParseException, NotAuthenticatedException;

	/**
	 * Get a binary by the uri. No security available; the method will fail if a SecurityFilter is configured on the factory.
	 * @param uri
	 * @return
	 * @throws ItemNotFoundException
	 * @throws ParseException 
	 * @throws StorageException 
	 */
	public Binary getBinary(String uri) throws ItemNotFoundException, StorageException, ParseException;

	/**
	 * Get a binary by the url and publicationId. The request context is used by the security filter (if there is one).
	 * @param url
	 * @param publicationId
	 * @param context
	 * @return
	 * @throws ItemNotFoundException
	 * @throws NotAuthorizedException
	 * @throws ParseException 
	 * @throws StorageException 
	 * @throws NotAuthenticatedException 
	 */
	public Binary findBinaryByUrl(String url, int publicationId, RequestContext context) throws ItemNotFoundException, NotAuthorizedException, StorageException, ParseException, NotAuthenticatedException;

	/**
	 * Get a binary by the url and publicationId. No security available; the method will fail if a SecurityFilter is configured on the factory.
	 * @param url
	 * @param publicationId
	 * @return
	 * @throws ItemNotFoundException
	 * @throws StorageException 
	 * @throws ParseException 
	 */
	public Binary findBinaryByUrl(String url, int publicationId) throws ItemNotFoundException, StorageException, ParseException;

	/**
	 * Load binary data into the assigned binary 
	 * @param binary
	 */
	public void retrieveBinaryData(Binary binary);

}

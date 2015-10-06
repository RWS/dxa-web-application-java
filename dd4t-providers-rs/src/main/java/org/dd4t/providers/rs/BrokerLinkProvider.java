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

package org.dd4t.providers.rs;

import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.providers.LinkProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Client side of the Link provider. The class handles communication with the service layer to resolve
 * Component links by their id. The communication is done using JAX-RS singleton client.
 * <p/>
 * The String response from the server represents a plain String representing the URL; or null if link not resolved.
 *
 * @author Mihai Cadariu
 */
public class BrokerLinkProvider extends BaseBrokerProvider implements LinkProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BrokerLinkProvider.class);

	public BrokerLinkProvider () {
		LOG.debug("Create new instance");
	}

	/**
	 * Retrieves a link URL to a Component. The JAX-RS client reads an plain String from the remote
	 * service in case the link was resolved; or null otherwise.
	 *
	 * @param targetComponentURI String representing the TcmUri of the Component to resolve a link to
	 * @return String representing the URL of the link; or null, if the Component is not linked to
	 */
	@Override
	public String resolveComponent (final String targetComponentURI) throws ItemNotFoundException, SerializationException {
		long time = System.currentTimeMillis();
		LOG.debug("Fetching link to Component with uri: {}", targetComponentURI);

		String result = null;
		try {
			WebTarget target = client.getResolveComponentTarget().path(targetComponentURI);
			result = target.request(MediaType.TEXT_PLAIN).get(String.class);
			result = result == null || result.length() == 0 ? null : result;
		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}

		time = System.currentTimeMillis() - time;
		LOG.debug("Finished fetching link. Duration {}s", time / 1000.0);

		return result;
	}

	/**
	 * Retrieves a link URL to a Component from a Page. The JAX-RS client reads an plain String from the remote
	 * service in case the link was resolved; or null otherwise.
	 *
	 * @param targetComponentURI String representing the TcmUri of the Component to resolve a link to
	 * @return String representing the URL of the link; or null, if the Component is not linked to
	 */
	@Override
	public String resolveComponentFromPage (final String targetComponentURI, final String sourcePageURI) throws ItemNotFoundException, SerializationException {
		long time = System.currentTimeMillis();
		LOG.debug("Fetching link to component: {} from page: {}", targetComponentURI, sourcePageURI);

		String result = null;
		try {
			WebTarget target = client.getResolveComponentFromPageTarget().path(targetComponentURI).path(sourcePageURI);
			result = target.request(MediaType.TEXT_PLAIN).get(String.class);
			result = result == null || result.length() == 0 ? null : result;
		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}

		time = System.currentTimeMillis() - time;
		LOG.debug("Finished fetching link. Duration {}s", time / 1000.0);

		return result;
	}

	@Override public String resolveComponent (final String targetComponentUri, final String componentTemplateUri) throws ItemNotFoundException, SerializationException {
		// TODO
		return null;
	}
}

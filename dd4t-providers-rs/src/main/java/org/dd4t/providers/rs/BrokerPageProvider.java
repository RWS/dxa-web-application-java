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
import org.dd4t.core.util.CompressionUtils;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.PageProvider;
import org.dd4t.providers.ProviderResultItem;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.text.ParseException;

/**
 * Client side of the Page provider. The class handles communication with the service layer to read
 * Pages by their URL or id. The communication is done using JAX-RS singleton client.
 * <p/>
 * The String response from the Tridion Service
 * represents a JSON Page model that has been GZip compressed,
 * then Base64 encoded.
 *
 * @author Mihai Cadariu, R. Kempees
 */
public class BrokerPageProvider extends BaseBrokerProvider implements PageProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BrokerPageProvider.class);

	public BrokerPageProvider () {
		LOG.debug("Create new instance");
	}


	@Override
	public ProviderResultItem<String> getPageById (final int id, final int publication) throws IOException, ItemNotFoundException, SerializationException {
		return null;
	}

	@Override
	public ProviderResultItem<String> getPageByURL (final String url, final int publication) throws ItemNotFoundException, SerializationException {
		return null;
	}

	@Override public String getPageContentById (final int id, final int publication) throws ItemNotFoundException {

		TCMURI pageUri = new TCMURI(publication, id, 64, -1);
		try {
			return getPageContentById(pageUri.toString());
		} catch (SerializationException | ParseException e) {
			throw new ItemNotFoundException(e);
		}
	}

	/**
	 * Retrieves a Page by its Publication and URL. The JAX-RS client reads an encoded String from the remote
	 * service and then proceeds to deserialize it into JSON representing a Page model object.
	 *
	 * @param url           String representing the path part of the page URL
	 * @param publicationId int representing the context Publication id to read the Page from
	 * @return String representing the JSON encoded Page model object
	 * @throws ItemNotFoundException  if said binary cannot be found
	 * @throws SerializationException if response from service does not represent a serialized Page
	 */
	@Override
	public String getPageContentByURL (final String url, final int publicationId) throws ItemNotFoundException, SerializationException {
		long time = System.currentTimeMillis();
		LOG.debug("Fetching page by url: {} and publicationId: {}", url, publicationId);

		String publication = String.valueOf(publicationId);
		String encodedURL = CompressionUtils.encodeBase64(url);
		LOG.debug("Encoded URL: {}", encodedURL);

		try {
			Invocation.Builder builder = client.getPageContentByURLTarget().path(publication).path(encodedURL).request(MediaType.TEXT_PLAIN);
			builder = getSessionPreviewBuilder(builder);

			String content = builder.get(String.class);
			if (content == null || content.length() == 0) {
				throw new ItemNotFoundException("Cannot find Page for Publication " + publication + " and URL " + url);
			}

			String result = decodeAndDecompressContent(content);

			time = System.currentTimeMillis() - time;
			LOG.debug("Finished fetching Page. Duration {}s", time / 1000.0);

			return result;
		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * Retrieves a Page by its TCMURI. The JAX-RS client reads an encoded String from the remote
	 * service and then proceeds to deserialize it into JSON representing a Page model object.
	 *
	 * @param tcmUri String representing the Tridion Page URI
	 * @return String representing the JSON encoded Page model object
	 * @throws ItemNotFoundException  if said binary cannot be found
	 * @throws ParseException         if given parameter does not represent a TCMURI
	 * @throws SerializationException if response from service does not represent a serialized Page
	 */
	@Override
	public String getPageContentById (final String tcmUri) throws ItemNotFoundException, SerializationException, ParseException {
		long time = System.currentTimeMillis();
		LOG.debug("Fetching page by uri: {}", tcmUri);

		TCMURI pageUri = new TCMURI(tcmUri);
		String publication = String.valueOf(pageUri.getPublicationId());
		String id = String.valueOf(pageUri.getItemId());

		try {
			Invocation.Builder builder = client.getPageContentByURLTarget().path(publication).path(id).request(MediaType.TEXT_PLAIN);
			builder = getSessionPreviewBuilder(builder);

			String content = builder.get(String.class);
			if (content == null || content.length() == 0) {
				throw new ItemNotFoundException("Cannot find Page for TCMURI " + tcmUri);
			}

			String result = decodeAndDecompressContent(content);

			time = System.currentTimeMillis() - time;
			LOG.debug("Finished fetching page. Duration {}s", time / 1000.0);

			return result;
		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public String getPageListByPublicationId (int publication) throws ItemNotFoundException, SerializationException {
		long time = System.currentTimeMillis();
		LOG.debug("Fetching page URL list by publication: {}", publication);

		try {
			Invocation.Builder builder = client.getPageListByPublicationTarget().path(String.valueOf(publication)).request(MediaType.TEXT_PLAIN);

			String content = builder.get(String.class);
			if (content == null || content.length() == 0) {
				throw new ItemNotFoundException("Cannot find Page URL list for publication " + publication);
			}

			String result = decodeAndDecompressContent(content);

			time = System.currentTimeMillis() - time;
			LOG.debug("Finished fetching page URL list. Duration {}s", time / 1000.0);

			return result;
		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * Checks whether a page exists (published from Tridion) by querying its URL
	 *
	 * @param url           String representing the path part of the page URL
	 * @param publicationId int representing the context Publication id to read the Page from
	 * @return Boolean True if the page is published and exists
	 * @throws ItemNotFoundException  if said page cannot be found
	 * @throws SerializationException if there was an error communicating with the service
	 */
	@Override
	public boolean checkPageExists (final String url, final int publicationId) throws ItemNotFoundException, SerializationException {
		long time = System.currentTimeMillis();
		LOG.debug("Checking Page existance by url: {} and publicationId: {}", url, publicationId);

		Integer exists;
		String publication = String.valueOf(publicationId);
		String encodedURL = CompressionUtils.encodeBase64(url);
		LOG.debug("Encoded URL: {}", encodedURL);

		try {
			WebTarget target = client.getPageCheckExists().path(publication).path(encodedURL);
			exists = target.request(MediaType.TEXT_PLAIN).get(Integer.class);
		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}

		time = System.currentTimeMillis() - time;
		LOG.debug("Finished checking page existance. Duration {}s", time / 1000.0);

		return exists == 1;
	}

	// TODO
	@Override
	public TCMURI getPageIdForUrl (final String url, final int publicationId) throws ItemNotFoundException, SerializationException {
		LOG.error("Not implemented yet!");
		return null;
	}

	// TODO
	@Override
	public DateTime getLastPublishDate (final String url, final int publication) throws ItemNotFoundException {
		return null;
	}
}

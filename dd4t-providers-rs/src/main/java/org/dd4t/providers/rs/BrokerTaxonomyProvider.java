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
import org.dd4t.providers.TaxonomyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;

/**
 * Client side of the Taxonomy provider. This communicates with the service layer to read
 * Taxonomies by their URI. The communication is done using JAX-RS singleton client.
 * <p/>
 * The String response represents a Taxonomy object that has been GZip compressed, then Base64 encoded.
 *
 * @author Mihai Cadariu
 */
public class BrokerTaxonomyProvider extends BaseBrokerProvider implements TaxonomyProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BrokerTaxonomyProvider.class);

	/**
	 * Retrieves a Taxonomy TCMURI. It returns a Keyword object representing the root taxonomy node with all the parent/
	 * children relationships resolved.
	 *
	 * @param taxonomyURI    String representing the TCMURI of the taxonomy to retrieve
	 * @param resolveContent boolean indicating whether or not to resolverepresenting the context Publication id to read the Page from
	 * @return String representing the JSON encoded Keyword object
	 * @throws ItemNotFoundException  if said taxonomy cannot be found
	 * @throws SerializationException if response from service does not represent a serialized Keyword object
	 */
	@Override
	public String getTaxonomyByURI (final String taxonomyURI, final boolean resolveContent) throws ItemNotFoundException, SerializationException {
		long time = System.currentTimeMillis();
		LOG.debug("Fetching taxonomy with uri: {}", taxonomyURI);

		try {
			Invocation.Builder builder = client.getTaxonomyByURITarget().
					path(taxonomyURI).path(String.valueOf(resolveContent)).request(MediaType.TEXT_PLAIN);
			builder = getSessionPreviewBuilder(builder);
			String content = builder.get(String.class);

			if (content == null || content.length() == 0) {
				throw new ItemNotFoundException("Cannot find Taxonomy for URI " + taxonomyURI);
			}

			String result = decodeAndDecompressContent(content);

			time = System.currentTimeMillis() - time;
			LOG.debug("Finished fetching taxonomy. Duration {}s", time / 1000.0);

			return result;
		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * Retrieves a Taxonomy TCMURI. It returns a Keyword object representing the root taxonomy node with all the parent/
	 * children relationships resolved. The related items are filtered to only Components based on the given Schema URI.
	 *
	 * @param taxonomyURI String representing the TCMURI of the taxonomy to retrieve
	 * @param schemaURI   String representing the filter for classified related Components to return for each Keyword
	 * @return String representing the JSON encoded Keyword object
	 * @throws ItemNotFoundException  if said taxonomy cannot be found
	 * @throws SerializationException if response from service does not represent a serialized Keyword object
	 */
	@Override
	public String getTaxonomyFilterBySchema (final String taxonomyURI, final String schemaURI) throws ItemNotFoundException, SerializationException {
		long time = System.currentTimeMillis();
		LOG.debug("Fetching taxonomy with uri: {} and schema: {}", taxonomyURI, schemaURI);

		try {
			Invocation.Builder builder = client.getTaxonomyBySchemaTarget().path(taxonomyURI).path(schemaURI).request(MediaType.TEXT_PLAIN);
			builder = getSessionPreviewBuilder(builder);
			String content = builder.get(String.class);

			if (content == null || content.length() == 0) {
				throw new ItemNotFoundException("Cannot find Taxonomy for URI " + taxonomyURI + " and filter schemaURI " + schemaURI);
			}

			String result = decodeAndDecompressContent(content);

			time = System.currentTimeMillis() - time;
			LOG.debug("Finished fetching taxonomy. Duration {}s", time / 1000.0);

			return result;
		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}
}

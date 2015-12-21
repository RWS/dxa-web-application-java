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

import org.dd4t.contentmodel.Binary;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.serializers.impl.BinaryBuilder;
import org.dd4t.core.util.CompressionUtils;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.BinaryProvider;
import org.dd4t.providers.transport.BinaryWrapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;

/**
 * Client side of the Binary provider. The class handles communication with the service layer to read
 * Binaries by their URL or id. The communication is done using JAX-RS singleton client.
 *
 * @author Mihai Cadariu
 */
public class BrokerBinaryProvider extends BaseBrokerProvider implements BinaryProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BrokerBinaryProvider.class);

	public BrokerBinaryProvider () {
		LOG.debug("Create new instance");
	}

	/**
	 * Retrieves a Binary by its Publication and URL. The JAX-RS client reads a byte array from the remote
	 * service and then proceeds to deserialize it into a Binary object.
	 *
	 * @param url           String representing the path part of the binary URL
	 * @param publicationId int representing the context Publication id to read the Binary from
	 * @return Binary a full binary object including metadata and binary data as byte array
	 * @throws ItemNotFoundException  if said binary cannot be found
	 * @throws SerializationException if response from service does not represent a serialized Binary
	 */
	@Override
	public Binary getBinaryByURL (final String url, final int publicationId) throws ItemNotFoundException, SerializationException {
		try {

			byte[] content = getBinaryContentByURL(url,publicationId);

			if (content == null || content.length == 0) {
				throw new ItemNotFoundException("Cannot find Binary for Publication " + publicationId + " and URL " + url);
			}

			return deserialize(content);

		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override public byte[] getBinaryContentByURL (final String url, final int publication) throws ItemNotFoundException {
		long time = System.currentTimeMillis();
		LOG.debug("Fetching binary by url: {}, and publication: {}", url, publication);

		String publicationId = String.valueOf(publication);
		String encodedURL = CompressionUtils.encodeBase64(url);

		Invocation.Builder builder = client.getBinaryWrapperByURLTarget().path(publicationId).path(encodedURL).request(MediaType.APPLICATION_OCTET_STREAM);
		builder = getSessionPreviewBuilder(builder);

		byte[] content = builder.get((new byte[0]).getClass());

		time = System.currentTimeMillis() - time;
		LOG.debug("Finished fetching binary. Duration {}s", time / 1000.0);
		return content;
	}

	/**
	 * Retrieves a Binary by its TCMURI. The JAX-RS client reads a byte array from the remote
	 * service and then proceeds to deserialize it into a Binary object.
	 *
	 * @param tcmUri String representing the Tridion binary URI
	 * @return Binary a full binary object including metadata and binary data as byte array
	 * @throws ItemNotFoundException  if said binary cannot be found
	 * @throws ParseException         if given parameter does not represent a TCMURI
	 * @throws SerializationException if response from service does not represent a serialized Binary
	 */
	@Override
	public Binary getBinaryByURI (final String tcmUri) throws ItemNotFoundException, ParseException, SerializationException {

		LOG.debug("Fetching binary by tcmuri: {}", tcmUri);

		TCMURI binaryURI = new TCMURI(tcmUri);

		try {

			byte[] content = getBinaryContentById(binaryURI.getItemId(),binaryURI.getPublicationId());
			return deserialize(content);

		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override public byte[] getBinaryContentById (final int id, final int publication) throws ItemNotFoundException {

		String publicationId = String.valueOf(publication);
		String itemId = String.valueOf(id);


		Invocation.Builder builder = client.getBinaryWrapperByIdTarget().path(publicationId).path(itemId).request(MediaType.APPLICATION_OCTET_STREAM);
		builder = getSessionPreviewBuilder(builder);

		byte[] content = builder.get((new byte[0]).getClass());
		if (content == null || content.length == 0) {
			throw new ItemNotFoundException("Cannot find Binary for TCMURI " + publicationId +"-"+itemId);
		}
		return content;
	}

	/**
	 * Checks whether a Publication with the given Images URL exists and returns the Publication TCMURI item id.
	 *
	 * @param imagesURL String representing the images URL to check
	 * @return int representing the item id of the Publication with Images URL or 0, otherwise
	 * @throws SerializationException if there was an error communicating with the service
	 */

	public int discoverPublicationId (final String imagesURL) throws SerializationException {
		long time = System.currentTimeMillis();
		LOG.debug("Discovering Publication id for ImagesUrl: {}", imagesURL);

		try {
			int result = client.getDiscoverPublicationByImagesURLTarget().path(CompressionUtils.encodeBase64(imagesURL)).request(MediaType.TEXT_PLAIN).get(Integer.class);

			time = System.currentTimeMillis() - time;
			LOG.debug("Finished discovering Publication id. Duration {}s", time / 1000.0);

			return result;
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}

	// TODO
	@Override
	public DateTime getLastPublishDate (final String tcmUri) throws ParseException, ItemNotFoundException {
		return null;
	}

	/*
		Deserializes the byte array into a Binary object. It is expected that byte array represents a GZipped BinaryWrapper
		object. Once deserialized, a Binary object is built from BinaryWrapper.
		 */
	private Binary deserialize (byte[] content) throws SerializationException {
		BinaryWrapper wrapper = CompressionUtils.decompressGZipGeneric(content);
		BinaryBuilder builder = new BinaryBuilder();
		return builder.build(wrapper);
	}
}

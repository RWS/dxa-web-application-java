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

import org.apache.commons.codec.binary.Base64;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.CompressionUtils;
import org.dd4t.core.util.TridionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ws.rs.client.Invocation;
import java.io.UnsupportedEncodingException;

/**
 * Base class for providers defining common utility methods for handling compression and encoding.
 *
 * @author R. Kempees
 */
public abstract class BaseBrokerProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BaseBrokerProvider.class);
	private final Base64 urlCoder = new Base64(true);
	private String baseRemoteUrl;

	// Set these values in Spring configuration
	protected boolean contentIsCompressed = true;
	protected final boolean contentIsBase64Encoded = true;
	@Resource
	protected JAXRSClient client;
	/**
	 * Set the status of content compression
	 *
	 * @param contentIsCompressed String representing a boolean value
	 */
	public void setContentIsCompressed (final String contentIsCompressed) {
		this.contentIsCompressed = Boolean.parseBoolean(contentIsCompressed);
	}

	/**
	 * Performs a Base64 decode of the given content String. If property <b>contentIsCompressed</b> is true, it then
	 * decompresses the message using GZip and returns it. Otherwise, it returns a UTF-8 string of the Base64 decoded
	 * string.
	 *
	 * @param content String to decode and decompress
	 * @return String the decoded/decompressed content
	 * @throws SerializationException if the given content cannot be decoded or decompressed
	 */
	protected String decodeAndDecompressContent (final String content) throws SerializationException {
		try {
			if (!contentIsBase64Encoded) {
				return content;
			}

			LOG.debug("Start decoding.");
			byte[] decoded = CompressionUtils.decodeBase64(content);

			if (contentIsCompressed) {
				LOG.debug("Start decompressing");
				return CompressionUtils.decompressGZip(decoded);
			}

			LOG.debug("End decoding and decompressing");
			return new String(decoded, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			throw new SerializationException("Failed to convert bytes to UTF-8", uee);
		}
	}

	/**
	 * Encodes the given URL parameter to Base64 format
	 *
	 * @param url String representing the message to encode
	 * @return String the Base64 encoded string
	 */
	protected String encodeUrl (final String url) {

		if (url == null) {
			return "";
		}

		String encoded = urlCoder.encodeAsString(url.getBytes());
		if (encoded == null) {
			return "";
		}
		return encoded.trim();
	}

	/**
	 * Inserts a cookie for with the Preview Session Token into the Invocation.Builder object (if available)
	 *
	 * @param builder Invocation.Builder object to insert cookie into
	 * @return Invocation.Builder object with the token cookie insterted
	 */
	protected Invocation.Builder getSessionPreviewBuilder (Invocation.Builder builder) {
		String sessionPreviewToken = TridionUtils.getSessionPreviewToken();

		if (sessionPreviewToken != null) {
			builder = builder.cookie(TridionUtils.PREVIEW_SESSION_TOKEN, sessionPreviewToken);
		}

		return builder;
	}

	public String getBaseRemoteUrl () {
		return baseRemoteUrl;
	}

	public void setBaseRemoteUrl (final String baseRemoteUrl) {
		this.baseRemoteUrl = baseRemoteUrl;
	}
}

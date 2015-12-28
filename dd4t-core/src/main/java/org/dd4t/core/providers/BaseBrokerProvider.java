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

package org.dd4t.core.providers;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.CharEncoding;
import org.dd4t.core.caching.CacheType;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.CompressionUtils;
import org.dd4t.providers.PayloadCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Base class for providers defining common utility methods for handling compression and encoding.
 *
 * @author R. Kempees
 */
public abstract class BaseBrokerProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BaseBrokerProvider.class);
    private static final Base64 URL_CODER = new Base64(true);

    // Set these values in Spring configuration
    protected boolean contentIsCompressed = true;
    protected final boolean contentIsBase64Encoded = true;
    @Resource
    protected PayloadCacheProvider cacheProvider;

    public static String convertStreamToString (InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, CharEncoding.UTF_8));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

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
            return new String(decoded, CharEncoding.UTF_8);
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

        String encoded = null;
        try {
            encoded = URL_CODER.encodeAsString(url.getBytes(CharEncoding.UTF_8));
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        if (encoded == null) {
            return "";
        }
        return encoded.trim();
    }

    /**
     * Builds a key using a named cache type (region) and a URL. This type of key is used to point to
     * actual payload in the cache. Use this key when looking up objects cached for a particular URL.
     *
     * @param type CacheType representing the type (or region) where the associated item is in cache
     * @param url  the path part of the URL of a Tridion item
     * @return String representing the key pointing to a URL value
     */
    protected String getKey (CacheType type, String url) {
        return String.format("%s-%s", type, url);
    }


    public void setCacheProvider (final PayloadCacheProvider cacheProvider) {
        this.cacheProvider = cacheProvider;
    }
}

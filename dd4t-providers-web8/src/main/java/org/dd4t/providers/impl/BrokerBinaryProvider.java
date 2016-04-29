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

package org.dd4t.providers.impl;

import com.sdl.web.api.content.BinaryContentRetriever;
import com.sdl.web.api.dynamic.BinaryContentRetrieverImpl;
import com.sdl.web.api.meta.WebBinaryMetaFactory;
import com.sdl.web.api.meta.WebBinaryMetaFactoryImpl;
import com.sdl.web.api.meta.WebComponentMetaFactory;
import com.sdl.web.api.meta.WebComponentMetaFactoryImpl;
import com.tridion.data.BinaryData;
import com.tridion.meta.BinaryMeta;
import com.tridion.meta.ComponentMeta;
import org.dd4t.contentmodel.Binary;
import org.dd4t.contentmodel.impl.BinaryDataImpl;
import org.dd4t.contentmodel.impl.BinaryImpl;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.Constants;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.BaseBrokerProvider;
import org.dd4t.providers.BinaryProvider;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides access to Binaries stored in the Content Delivery database through the Web 8
 * REST client.
 * Access to these objects is not cached, and as such must be cached externally.
 */
public class BrokerBinaryProvider extends BaseBrokerProvider implements BinaryProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerBinaryProvider.class);

    private static final BinaryContentRetriever BINARY_CONTENT_RETRIEVER = new BinaryContentRetrieverImpl();
    private static final WebBinaryMetaFactory WEB_BINARY_META_FACTORY = new WebBinaryMetaFactoryImpl();
    private static final Map<Integer, WebComponentMetaFactory> FACTORY_CACHE = new ConcurrentHashMap<>();

    @Override
    public Binary getBinaryByURI (final String tcmUri) throws ItemNotFoundException, ParseException, SerializationException {
        final TCMURI binaryUri = new TCMURI(tcmUri);
        final BinaryMeta binaryMeta = getBinaryMetaByTcmUri(binaryUri);
        return getBinary(binaryUri, binaryMeta);
    }

    @Override
    public Binary getBinaryByURL (final String url, final int publication) throws ItemNotFoundException, SerializationException {
        final BinaryMeta binaryMeta = getBinaryMetaByURL(url, publication);

        if (binaryMeta == null) {
            throw new ItemNotFoundException("Unable to find binary content by URL '" + url + "' and publication '" + publication + "'.");
        }
        TCMURI binaryUri = new TCMURI(binaryMeta.getPublicationId(), (int)binaryMeta.getId(), 16);
        return getBinary(binaryUri, binaryMeta);
    }

    // TODO: test
    private Binary getBinary (final TCMURI binaryUri, final BinaryMeta binaryMeta) throws ItemNotFoundException {
        if (binaryMeta != null) {
            final BinaryImpl binary = new BinaryImpl();


            binary.setId(binaryUri.toString());
            binary.setUrlPath(binaryMeta.getURLPath());
            binary.setMimeType(binaryMeta.getType());

            binary.setLastPublishedDate(getLastPublishDate(binaryUri.toString()));

            // TODO: binary.setMetadata(binaryMeta.getCustomMeta().getChildren());
            final BinaryDataImpl binaryDataBytes = new BinaryDataImpl();
            binaryDataBytes.setBytes(getBinaryContentById(binaryUri.getItemId(),binaryUri.getPublicationId()));
            binary.setBinaryData(binaryDataBytes);
            return binary;
        }
        return null;
    }

    /**
     * Retrieves the byte array content of a Tridion binary based on its TCM item id and publication id.
     *
     * @param id          int representing the item id
     * @param publication int representing the publication id
     * @return byte[] the byte array of the binary content
     * @throws ItemNotFoundException if the item identified by id and publication was not found
     */
    @Override
    public byte[] getBinaryContentById (int id, int publication) throws ItemNotFoundException {

        final BinaryData binaryData = BINARY_CONTENT_RETRIEVER.getBinary(publication,id);

        if (binaryData == null || binaryData.getDataSize() == 0 ) {
            throw new ItemNotFoundException("Unable to find binary content by id: tcm:" + publication+"-"+id);
        }

        try {
            return binaryData.getBytes().clone();
        } catch (IOException e) {
            // TODO: wrap in provider exception
            throw new ItemNotFoundException(e);
        }
    }

    /**
     * Retrieves the byte array content of a Tridion binary based on its URL.
     *
     * @param url         string representing the path portion of the URL of the binary
     * @param publication int representing the publication id
     * @return byte[] the byte array of the binary content
     * @throws ItemNotFoundException if the item identified by id and publication was not found
     */
    @Override
    public byte[] getBinaryContentByURL (String url, int publication) throws ItemNotFoundException {

        BinaryMeta binaryMeta = getBinaryMetaByURL(url, publication);
        // TODO: check if long to int cast is correct
        return getBinaryContentById((int) binaryMeta.getId(), binaryMeta.getPublicationId());
    }

    /**
     * @param id          int representing the item id
     * @param publication int representing the publication id
     * @return BinaryData the binary identified by id and publication
     * @throws ItemNotFoundException if the item identified by id and publication was not found
     */

    public BinaryData getBinaryDataById (int id, int publication) throws ItemNotFoundException {
        final BinaryData binaryData = BINARY_CONTENT_RETRIEVER.getBinary(publication,id);
        if (binaryData == null) {
            throw new ItemNotFoundException("Unable to find binary by id '" + id + "' and publication '" + publication + "'.");
        }
        return binaryData;
    }

    /**
     * @param url         string representing the path portion of the URL of the binary
     * @param publication int representing the publication id
     * @return BinaryMeta the binary identified by url and publication
     * @throws ItemNotFoundException if the item identified by url and publication was not found
     */

    public BinaryMeta getBinaryMetaByURL (String url, int publication) throws ItemNotFoundException {
        final BinaryMeta binaryMeta = WEB_BINARY_META_FACTORY.getMetaByURL(publication,url);
        if (binaryMeta == null) {
            throw new ItemNotFoundException("Unable to find binary by url '" + url + "' and publication '" + publication + "'.");
        }
        return binaryMeta;
    }

    public BinaryMeta getBinaryMetaById(int publicationId, int itemId) throws ItemNotFoundException {
        TCMURI binaryUri = new TCMURI(publicationId, itemId, 16);
        return getBinaryMetaByTcmUri(binaryUri);
    }

    public BinaryMeta getBinaryMetaByTcmUri(TCMURI binaryUri) throws ItemNotFoundException {
        BinaryMeta binaryMeta = WEB_BINARY_META_FACTORY.getMeta(binaryUri.toString());
        if (binaryMeta == null) {
            throw new ItemNotFoundException("Unable to find binary by TCMURI '" + binaryUri.toString());
        }
        return binaryMeta;
    }


    @Override
    public DateTime getLastPublishDate (String tcmUri) throws ItemNotFoundException {
        TCMURI binaryTcmUri = null;
        try {
            binaryTcmUri = new TCMURI(tcmUri);
        } catch (ParseException e) {
            LOG.error(e.getLocalizedMessage(),e);
        }

        if (binaryTcmUri == null) {
            return Constants.THE_YEAR_ZERO;
        }
        WebComponentMetaFactory webComponentMetaFactory = FACTORY_CACHE.get(binaryTcmUri.getPublicationId());

        if (webComponentMetaFactory == null) {
            webComponentMetaFactory = new WebComponentMetaFactoryImpl(binaryTcmUri.getPublicationId());
            FACTORY_CACHE.put(binaryTcmUri.getPublicationId(),webComponentMetaFactory);
        }

        final ComponentMeta binaryMeta = webComponentMetaFactory.getMeta(tcmUri);

        if (binaryMeta != null && binaryMeta.getLastPublicationDate() != null) {
            return new DateTime(binaryMeta.getLastPublicationDate());
        }
        return Constants.THE_YEAR_ZERO;
    }
}

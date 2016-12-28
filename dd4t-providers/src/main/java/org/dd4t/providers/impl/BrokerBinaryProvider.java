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

import com.tridion.broker.StorageException;
import com.tridion.storage.BinaryContent;
import com.tridion.storage.BinaryVariant;
import com.tridion.storage.StorageManagerFactory;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.BinaryContentDAO;
import com.tridion.storage.dao.BinaryVariantDAO;
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

import java.text.ParseException;
import java.util.Date;

/**
 * Provides access to Binaries stored in the Content Delivery database. It uses JPA DAOs to retrieve raw binary content
 * or binary metadata from the database. Access to these objects is not cached, and as such must be cached externally.
 */
public class BrokerBinaryProvider extends BaseBrokerProvider implements BinaryProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerBinaryProvider.class);

    @Override
    public Binary getBinaryByURI (final String tcmUri) throws ItemNotFoundException, ParseException, SerializationException {
        final TCMURI binaryUri = new TCMURI(tcmUri);
        final BinaryVariant binaryVariant = getBinaryVariantById(binaryUri.getItemId(), binaryUri.getPublicationId());
        return getBinary(binaryUri, binaryVariant);
    }


    @Override
    public Binary getBinaryByURL (final String url, final int publication) throws ItemNotFoundException, SerializationException {
        final BinaryVariant binaryVariant = getBinaryVariantByURL(url, publication);
        final TCMURI binaryUri = new TCMURI(binaryVariant.getBinaryMeta().getPublicationId(), binaryVariant.getBinaryMeta().getItemId(), 16);
        return getBinary(binaryUri, binaryVariant);
    }

    private static Binary getBinary (final TCMURI binaryUri, final BinaryVariant binaryVariant) throws ItemNotFoundException {
        if (binaryVariant != null) {
            final BinaryImpl binary = new BinaryImpl();
            binary.setId(binaryUri.toString());
            binary.setUrlPath(binaryVariant.getUrl());
            binary.setMimeType(binaryVariant.getBinaryType());

            if (binaryVariant.getBinaryMeta() != null) {
                final Date lastPublishDate = binaryVariant.getBinaryMeta().getMultimediaMeta().getLastPublishDate();

                if (lastPublishDate != null) {
                    binary.setLastPublishedDate(new DateTime(lastPublishDate));
                } else {
                    binary.setLastPublishedDate(Constants.THE_YEAR_ZERO);
                }

                final Date revisionDate = binaryVariant.getBinaryMeta().getMultimediaMeta().getModificationDate();
                if (revisionDate != null) {
                    binary.setRevisionDate(new DateTime(revisionDate));
                } else {
                    binary.setRevisionDate(Constants.THE_YEAR_ZERO);
                }


                // TODO: binaryMeta.getCustomMeta();
                //binaryMeta.getDescription();
                //binaryMeta.getPath();
                //binaryMeta.getVariantId();

            }
            final BinaryContentDAO contentDAO;
            BinaryContent content = null;
            try {
                contentDAO = (BinaryContentDAO) StorageManagerFactory.getDAO(binaryUri.getPublicationId(), StorageTypeMapping.BINARY_CONTENT);
                content = contentDAO.findByPrimaryKey(binaryUri.getPublicationId(), binaryUri.getItemId(), null);
            } catch (StorageException e) {
                LOG.error(e.getMessage(), e);
            }


            if (content == null) {
                throw new ItemNotFoundException("Unable to find binary content by id:" + binaryUri.toString());
            }

            final BinaryDataImpl binaryData = new BinaryDataImpl();
            binaryData.setBytes(content.getContent().clone());
            binary.setBinaryData(binaryData);
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
        BinaryContentDAO contentDAO;
        BinaryContent content = null;
        try {
            contentDAO = (BinaryContentDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.BINARY_CONTENT);
            content = contentDAO.findByPrimaryKey(publication, id, null);
        } catch (StorageException e) {
            LOG.error(e.getMessage(), e);
        }


        if (content == null) {
            throw new ItemNotFoundException("Unable to find binary content by id '" + id + "' and publication '" + publication + "'.");
        }

        return content.getContent();
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

        BinaryVariant variant = getBinaryVariantByURL(url, publication);
        return getBinaryContentById(variant.getBinaryId(), variant.getPublicationId());
    }

    /**
     * @param id          int representing the item id
     * @param publication int representing the publication id
     * @return BinaryVariant the binary identified by id and publication
     * @throws ItemNotFoundException if the item identified by id and publication was not found
     */

    public BinaryVariant getBinaryVariantById (int id, int publication) throws ItemNotFoundException {

        BinaryVariant variant = null;
        try {
            BinaryVariantDAO variantDAO = (BinaryVariantDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.BINARY_VARIANT);
            variant = variantDAO.findByPrimaryKey(publication, id, null);
        } catch (StorageException e) {
            LOG.error(e.getMessage(), e);
        }

        if (variant == null) {
            throw new ItemNotFoundException("Unable to find binary by id '" + id + "' and publication '" + publication + "'.");
        }

        return variant;
    }

    /**
     * @param url         string representing the path portion of the URL of the binary
     * @param publication int representing the publication id
     * @return BinaryVariant the binary identified by url and publication
     * @throws ItemNotFoundException if the item identified by url and publication was not found
     */

    public BinaryVariant getBinaryVariantByURL (String url, int publication) throws ItemNotFoundException {

        BinaryVariant variant = null;
        try {
            BinaryVariantDAO variantDAO = (BinaryVariantDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.BINARY_VARIANT);
            variant = variantDAO.findByURL(publication, url);
        } catch (StorageException e) {
            LOG.error(e.getMessage(), e);
        }

        if (variant == null) {
            throw new ItemNotFoundException("Unable to find binary by url '" + url + "' and publication '" + publication + "'.");
        }

        return variant;
    }

    @Override
    public DateTime getLastPublishDate (String tcmUri) throws ParseException, ItemNotFoundException {
        TCMURI binaryTcmUri = new TCMURI(tcmUri);
        BinaryVariant variant = getBinaryVariantById(binaryTcmUri.getItemId(), binaryTcmUri.getPublicationId());

        if (variant != null && variant.getBinaryMeta() != null) {
            Date lpd = variant.getBinaryMeta().getMultimediaMeta().getLastPublishDate();
            if (lpd != null) {
                return new DateTime(lpd);
            }
        }
        return new DateTime(0, 0, 0, 0, 0);
    }
}

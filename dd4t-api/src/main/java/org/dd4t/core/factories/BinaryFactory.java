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

package org.dd4t.core.factories;

import org.dd4t.contentmodel.Binary;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;

/**
 * Interface for factories that return binary items (e.g. images, office documents).
 *
 * @author Quirijn Slings
 */
public interface BinaryFactory extends Factory {

    /**
     * Get a binary by the tcmUri.
     *
     * @param tcmUri String representing the TCMURI of the binary to retrieve
     * @return Binary object containing the metadata and raw byte array content
     * @throws ItemNotFoundException  if the items cannot be found in the underlying storage
     * @throws SerializationException if the binary cannot be deserialized into an object
     */
    Binary getBinaryByURI (String tcmUri) throws FactoryException;

    /**
     * Get a binary by the url and publicationId.
     *
     * @param url           String representing the path part of the binary URL
     * @param publicationId int representing the Publication context id
     * @return Binary object containing the metadata and raw byte array content
     * @throws ItemNotFoundException  if the items cannot be found in the underlying storage
     * @throws SerializationException if the binary cannot be deserialized into an object
     */
    Binary getBinaryByURL (String url, int publicationId) throws FactoryException;

}

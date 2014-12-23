package org.dd4t.core.factories;

import org.dd4t.contentmodel.Binary;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;

import java.text.ParseException;

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
     * @throws ParseException         if the TCMURI is not well formed
     * @throws SerializationException if the binary cannot be deserialized into an object
     */
    public Binary getBinaryByURI(String tcmUri) throws FactoryException;

    /**
     * Get a binary by the url and publicationId.
     *
     * @param url           String representing the path part of the binary URL
     * @param publicationId int representing the Publication context id
     * @return Binary object containing the metadata and raw byte array content
     * @throws ItemNotFoundException  if the items cannot be found in the underlying storage
     * @throws SerializationException if the binary cannot be deserialized into an object
     */
    public Binary getBinaryByURL(String url, int publicationId) throws FactoryException;

  }

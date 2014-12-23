package org.dd4t.providers;

import org.dd4t.contentmodel.Binary;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;

import java.text.ParseException;

/**
 * Binary provider client. This will communicate with either a service layer
 * or an implementation to the Tridion Broker API to read binaries by their URL or id.
 */
public interface BinaryProvider extends BaseProvider {

    /**
     * Retrieves a Binary by its TCMURI and deserializes it into a Binary object.
     *
     * @param tcmUri  String representing the Tridion Binary URI
     * @return Binary a full binary object including metadata and binary data as byte array
     * @throws ItemNotFoundException  if said binary cannot be found
     * @throws ParseException         if given parameter does not represent a TCMURI
     * @throws SerializationException if response from service does not represent a serialized Binary
     */
    public Binary getBinaryByURI(String tcmUri) throws ItemNotFoundException, ParseException, SerializationException;

    /**
     * Retrieves a Binary by its Publication and URL and deserializes it into a Binary object.
     *
     * @param url     String representing the path part of the binary URL
     * @return Binary a full binary object including metadata and binary data as byte array
     * @throws ItemNotFoundException  if said binary cannot be found
     * @throws SerializationException if response from service does not represent a serialized Binary
     */
    public Binary getBinaryByURL(String url, int publication) throws ItemNotFoundException, SerializationException;

	byte[] getBinaryContentById (int id, int publication) throws ItemNotFoundException;

	byte[] getBinaryContentByURL (String url, int publication) throws ItemNotFoundException;
}

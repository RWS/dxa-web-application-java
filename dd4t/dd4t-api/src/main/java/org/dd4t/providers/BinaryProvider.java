package org.dd4t.providers;

import org.dd4t.contentmodel.Binary;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;

import java.text.ParseException;

/**
 * Client side of the Binary provider. This will communicate  with the service layer (Hammerfest) to read
 * Binaris by their URL or id. The communication is done using JAX-RS singleton client.
 * <p/>
 * No Tridion dependencies are allowed here (they are all in Hammerfest)
 */
public interface BinaryProvider {

    /**
     * Retrieves a Binary by its TCMURI from the remote Hammerfest and deserializes it into a Binary object.
     *
     * @param tcmUri  String representing the Tridion Binary URI
     * @return Binary a full binary object including metadata and binary data as byte array
     * @throws ItemNotFoundException  if said binary cannot be found
     * @throws ParseException         if given parameter does not represent a TCMURI
     * @throws SerializationException if response from service does not represent a serialized Binary
     */
    public Binary getBinaryByURI(String tcmUri)
            throws ItemNotFoundException, ParseException, SerializationException;

    /**
     * Retrieves a Binary by its Publication and URL from the remote Hammerfest and deserializes it into a Binary object.
     *
     * @param url     String representing the path part of the binary URL
     * @return Binary a full binary object including metadata and binary data as byte array
     * @throws ItemNotFoundException  if said binary cannot be found
     * @throws SerializationException if response from service does not represent a serialized Binary
     */
    public Binary getBinaryByURL(String url, int publication)
            throws ItemNotFoundException, SerializationException;

    /**
     * Checks whether a Publication with the given Images URL exists and returns the Publication TCMURI item id.
     *
     * @param imagesURL String representing the images URL to check
     * @return int representing the item id of the Publication with Images URL or 0, otherwise
     * @throws SerializationException if there was an error communicating with the service
     */
    public int discoverPublicationId(String imagesURL) throws SerializationException;

    byte[] getBinaryContentById(int id, int publication) throws ItemNotFoundException;

    byte[] getBinaryContentByURL(String url, int publication) throws ItemNotFoundException;
}

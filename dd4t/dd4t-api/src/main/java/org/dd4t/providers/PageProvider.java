package org.dd4t.providers;

import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.core.request.RequestContext;

import java.io.IOException;
import java.text.ParseException;

/**
 * Client side of the Page provider. This will communicate  with the service layer (Hammerfest) to read
 * Pages by their URL or id. The communication is done using JAX-RS singleton client.
 * <p/>
 * No Tridion dependencies are allowed here (they are all in Hammerfest)
 */
public interface PageProvider {

    String getPageContentById(int id, int publication) throws IOException, ItemNotFoundException;

    /**
     * Retrieves a Page by its Publication and URL. It returns JSON representing a Page model object.
     *
     * @param url         String representing the path part of the page URL
     * @param publication int representing the context Publication id to read the Page from
     * @return String representing the JSON encoded Page model object
     * @throws ItemNotFoundException  if said page cannot be found
     * @throws SerializationException if response from service does not represent a serialized Page
     */
    public String getPageContentByURL(String url, int publication)
            throws ItemNotFoundException, IOException;

    /**
     * Retrieves a Page by its TCMURI. It returns JSON representing a Page model object.
     *
     * @param tcmUri  String representing the Tridion Page URI
     * @param context RequestContext object providing access to HttpServletRequest
     * @return String representing the JSON encoded Page model object
     * @throws ItemNotFoundException  if said page cannot be found
     * @throws ParseException         if given parameter does not represent a TCMURI
     * @throws SerializationException if response from service does not represent a serialized Page
     */
    public String getPageContentById(String tcmUri, RequestContext context)
            throws ItemNotFoundException, ParseException, SerializationException;

    String getPageContentById (String tcmUri) throws ItemNotFoundException, ParseException, SerializationException, IOException;

    public String getPageListByPublicationId(int publication) throws ItemNotFoundException, SerializationException;

    /**
     * Checks whether a page exists (published from Tridion) by querying its URL
     *
     * @param url           String representing the path part of the page URL
     * @param publicationId int representing the context Publication id to read the Page from
     * @return Boolean True if the page is published and exists
     * @throws ItemNotFoundException  if said page cannot be found
     * @throws SerializationException if there was an error communicating with the service
     */
    public Boolean checkPageExists(final String url, final int publicationId)
            throws ItemNotFoundException, SerializationException;

    /**
     * Checks whether a Publication with the given Publication URL exists and returns the Publication TCMURI item id.
     *
     * @param publicationURL String representing the Publication URL to check
     * @return int representing the item id of the Publication with Publication URL or 0, otherwise
     * @throws SerializationException if there was an error communicating with the service
     */
    public int discoverPublicationId(String publicationURL) throws SerializationException;
}

package org.dd4t.providers;

import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;

import java.io.IOException;
import java.text.ParseException;

/**
 * Page Provider.Reads Pages by their URL or id.
 */
public interface PageProvider extends BaseProvider {

	public String getPageContentById (int id, int publication) throws IOException, ItemNotFoundException, SerializationException;

	/**
     * Retrieves a Page by its Publication and URL. It returns JSON representing a Page model object.
     *
     * @param url         String representing the path part of the page URL
     * @param publication int representing the context Publication id to read the Page from
     * @return String representing the JSON encoded Page model object
     * @throws ItemNotFoundException  if said page cannot be found
     * @throws SerializationException if response from service does not represent a serialized Page
     */
    public String getPageContentByURL(String url, int publication) throws ItemNotFoundException, SerializationException, IOException;

    /**
     * Retrieves a Page by its TCMURI. It returns JSON representing a Page model object.
     *
     * @param tcmUri  String representing the Tridion Page URI
     * @return String representing the JSON encoded Page model object
     * @throws ItemNotFoundException  if said page cannot be found
     * @throws ParseException         if given parameter does not represent a TCMURI
     * @throws SerializationException if response from service does not represent a serialized Page
     */
    public String getPageContentById(String tcmUri) throws ItemNotFoundException, ParseException, SerializationException, IOException;

	/**
	 * Retrieves a list of published page URLs as one String.
	 *
	 * @param publication the Publication Id
	 * @return A String containing all publication URLs
	 * @throws ItemNotFoundException
	 * @throws SerializationException
	 */

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
    public Boolean checkPageExists(final String url, final int publicationId) throws ItemNotFoundException, SerializationException;

	/**
	 * Discovers the publication Id based on the URL
	 * @param publicationURL the current URL
	 * @return int representing the Tridion publication Id
	 * @throws SerializationException
	 */
	public int discoverPublicationId(final String publicationURL) throws SerializationException;
}

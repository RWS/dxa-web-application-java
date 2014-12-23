package org.dd4t.providers;

import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;

/**
 * Link provider.
 */
public interface LinkProvider {

    /**
     * Retrieves a link URL to a Component.
     *
     * @param targetComponentUri String representing the TcmUri of the Component to resolve a link to
     * @return String representing the URL of the link; or null, if the Component is not resolved.
     * @throws ItemNotFoundException
     * @throws SerializationException
     */
    public String resolveComponent(String targetComponentUri) throws ItemNotFoundException, SerializationException;

    /**
     * Retrieves a link URL to a Component from a Page.
     *
     * @param targetComponentUri String representing the TcmUri of the Component to resolve a link to
     * @return String representing the URL of the link; or null, if the Component is not resolved
     * @throws ItemNotFoundException
     * @throws SerializationException
     */
    public String resolveComponentFromPage(String targetComponentUri, String sourcePageUri) throws ItemNotFoundException, SerializationException;

	/**
	 * Retrieves a link URL to a Component with the option to exclude links
	 * to the specified component template.
	 *
	 * @param targetComponentUri String representing the TcmUri of the Component to resolve a link to
	 * @param componentTemplateUri The Component Presentation to exclude
	 * @return String representing the URL of the link; or null, if the Component is not resolved.
	 * @throws ItemNotFoundException
	 * @throws SerializationException
	 */
	public String resolveComponent(String targetComponentUri,String componentTemplateUri) throws ItemNotFoundException, SerializationException;
}

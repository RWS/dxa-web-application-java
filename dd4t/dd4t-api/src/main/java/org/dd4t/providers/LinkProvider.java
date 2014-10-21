package org.dd4t.providers;

import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;

/**
 * Client side of the Link provider. The class handles communication with the service layer (Hammerfest) to resolve
 * Component links by their id. The communication is done using JAX-RS singleton client.
 * <p/>
 * The String response from Hammerfest represents a plain String representin the URL; or null if link not resolved.
 */
public interface LinkProvider {

    /**
     * Retrieves a link URL to a Component. The JAX-RS client reads an plain String from the remote Hammerfest
     * service in case the link was resolved; or null otherwise.
     *
     * @param targetComponentURI String representing the TcmUri of the Component to resolve a link to
     * @return String representing the URL of the link; or null, if the Component is not linked to
     */
    public String resolveComponent(String targetComponentURI) throws ItemNotFoundException, SerializationException;

    /**
     * Retrieves a link URL to a Component from a Page. The JAX-RS client reads an plain String from the remote
     * Hammerfest service in case the link was resolved; or null otherwise.
     *
     * @param targetComponentURI String representing the TcmUri of the Component to resolve a link to
     * @return String representing the URL of the link; or null, if the Component is not linked to
     */
    public String resolveComponentFromPage(String targetComponentURI, String sourcePageURI) throws ItemNotFoundException, SerializationException;
}

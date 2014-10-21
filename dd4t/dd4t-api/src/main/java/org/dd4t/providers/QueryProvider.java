package org.dd4t.providers;

import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;

import java.util.Collection;
import java.util.Map;

public interface QueryProvider {

    /**
     * Retrieves content of a Component Presentation by looking up its locale and keyValueMap
     * The returned content represent multiple JSON encoded string separated by |.
     *
     * @param locale      String representing the locale to be queried, for e.g.: en_gb
     * @param keyValueMap A Map representing the Key Value pairs to insert as Meta Filters
     * @return String representing the content of the CP
     * @throws org.dd4t.contentmodel.exceptions.ItemNotFoundException  if the requested CP does not exist
     * @throws org.dd4t.contentmodel.exceptions.SerializationException if something went wrong during deserialization
     */
    String[] getComponentPresentationsByQuery(String locale, Map<String, Collection<String>> keyValueMap, int templateId) throws ItemNotFoundException, SerializationException;


    String[] getComponentPresentationsBySchema(String locale, String schema, int templateId) throws ItemNotFoundException, SerializationException;

	String[] getComponentPresentationsBySchemaInKeyword(String locale, String schema, int categoryId, int keywordId, int templateId) throws ItemNotFoundException, SerializationException;

    /**
     * Retrieves a link URL to a Component. The JAX-RS client reads an plain String from the remote Hammerfest
     * service in case the link was resolved; or null otherwise.
     *
     * @param targetComponentURI String representing the TcmUri of the Component to resolve a link to
     * @return String representing the URL of the link; or null, if the Component is not linked to
     */
    String resolveComponent(String targetComponentURI) throws ItemNotFoundException, SerializationException;

}

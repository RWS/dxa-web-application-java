package org.dd4t.core.factories;

import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.core.filters.FilterException;
import org.dd4t.core.request.RequestContext;

import java.io.IOException;
import java.text.ParseException;

// TODO: single exceptions
public interface PageFactory extends Factory {

    /**
     * Get a page by its URI. No security available; the method will fail if a
     * SecurityFilter is configured on the factory.
     *
     * @param uri of the page
     * @return
     * @throws ItemNotFoundException
     */
    public Page getPage(String uri)
            throws ItemNotFoundException, FilterException, ParseException, SerializationException;

    /**
     * Get a page by its URI. The request context is used by the security filter
     * (if there is one).
     *
     * @param uri     of the page
     * @param context (normally wrapped around the HttpServletRequest)
     * @return the Page Model
     * @throws ItemNotFoundException
     */
    public Page getPage(String uri, RequestContext context)
            throws ItemNotFoundException, FilterException, ParseException, SerializationException;

    /**
     * Find page by its URL. The url and publication id are specified. No
     * security available; the method will fail if a SecurityFilter is
     * configured on the factory.
     *
     * @return
     * @throws ItemNotFoundException
     */
    public Page findPageByUrl(String url, int publicationId) throws ItemNotFoundException, FilterException, ParseException, SerializationException, IOException;

    /**
     * Find XML page by its URL. The url and publication id are specified. No
     * security available.
     *
     * @return XML as string
     * @throws ItemNotFoundException
     * * Vinod Bhagat added on 13 September 2014
     */
    public String findXMLPageByUrl(String url, int publicationId) throws ItemNotFoundException, FilterException, ParseException, SerializationException, IOException;

}



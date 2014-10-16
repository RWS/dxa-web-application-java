package org.dd4t.core.filters;

import org.dd4t.contentmodel.Item;
import org.dd4t.contentmodel.exceptions.NotAuthenticatedException;
import org.dd4t.contentmodel.exceptions.NotAuthorizedException;
import org.dd4t.contentmodel.exceptions.SerializationException;

/**
 * Interface for filters.
 *
 * @author bjornl
 */
public interface Filter {

    /**
     * Execute the filter
     *
     * @param item
     * @throws FilterException
     * @throws NotAuthorizedException
     * @throws NotAuthenticatedException
     */
    public void doFilter(Item item) throws FilterException, SerializationException;

    /**
     * Returns if the result of the filter is allowed to be cached.
     *
     * @return true if the result of the filter is allowed to be cached.
     */
    public boolean getCachingAllowed();

    /**
     * Set if the result of the filter is allowed to be cached.
     */
    public void setCachingAllowed(boolean cachingAllowed);
}
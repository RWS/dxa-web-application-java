package org.dd4t.core.filters;

import org.dd4t.contentmodel.Item;
import org.dd4t.core.exceptions.FilterException;
import org.dd4t.core.exceptions.SerializationException;

public interface Filter {

    /**
     * Execute the filter
     *
     * @param item the Tridion item
     * @throws FilterException
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
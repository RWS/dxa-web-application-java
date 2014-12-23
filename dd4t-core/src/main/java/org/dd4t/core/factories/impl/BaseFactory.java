package org.dd4t.core.factories.impl;

import org.dd4t.contentmodel.Binary;
import org.dd4t.contentmodel.Item;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.filters.Filter;
import org.dd4t.core.exceptions.FilterException;
import org.dd4t.core.filters.LinkResolverFilter;
import org.dd4t.core.filters.impl.BaseFilter;
import org.dd4t.core.serializers.impl.SerializerFactory;
import org.dd4t.providers.CacheProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all factories. All factories have a list of filters and a
 * default cache agent.
 *
 * @author bjornl, rai
 */
public abstract class BaseFactory {

    @Autowired
    protected CacheProvider cacheProvider;
    private List<Filter> filters;

    public List<Filter> getFilters() {
        if (filters == null) {
            this.filters = new ArrayList<>();
        }
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = new ArrayList<>();

        for (Filter filter : filters) {
            this.filters.add(filter);
        }
    }

    /**
     * Runs all the filters on an item. If the cachingAllowed is true it will
     * only run the filters where the result is allowed to be cached.
     *
     * @param item The DD4T Item
     * @throws FilterException
     * @throws SerializationException
     */
    public void doFilters(Item item, BaseFilter.RunPhase runPhase) throws FilterException, SerializationException {
        if (item != null) {
            for (Filter filter : getFilters()) {
                if (runPhase == BaseFilter.RunPhase.BOTH) {
                    this.doFilter(filter, item);
                } else if (runPhase == BaseFilter.RunPhase.BEFORE_CACHING && filter.getCachingAllowed()) {
                    this.doFilter(filter, item);
                } else if (runPhase == BaseFilter.RunPhase.AFTER_CACHING && !filter.getCachingAllowed()) {
                    this.doFilter(filter, item);
                }
            }
        }
    }

    private void doFilter(Filter filter, Item item) throws FilterException, SerializationException {
        // link resolving is not needed for the simple objects or binary
        if (filter instanceof LinkResolverFilter && item instanceof Binary) {
            return;
        }

        filter.doFilter(item);
    }

    /**
     * Set the cache agent.
     */
    public void setCacheProvider(CacheProvider cacheAgent) {
        cacheProvider = cacheAgent;
    }

    /**
     * Deserializes a JSON encoded String into an object of the given type
     *
     * @param source String representing the JSON encoded object
     * @param clazz  Class representing the implementation type to deserialize into
     * @return the deserialized object
     */
    protected <T extends Item> T deserialize(String source, Class<? extends T> clazz) throws SerializationException {
        return SerializerFactory.deserialize(source, clazz);
    }
}
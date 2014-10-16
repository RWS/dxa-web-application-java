package org.dd4t.core.factories;

import org.dd4t.core.filters.Filter;
import org.dd4t.providers.CacheProvider;

import java.util.List;

public interface Factory {

    public List<Filter> getFilters();

    public void setFilters(List<Filter> filters);

    public void setCacheProvider(CacheProvider cacheAgent);
}

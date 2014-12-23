package org.dd4t.core.filters.impl;

import org.dd4t.core.filters.Filter;

public abstract class BaseFilter implements Filter {

    private boolean cachingAllowed = true;

    @Override
    public boolean getCachingAllowed() {
        return this.cachingAllowed;
    }

    @Override
    public void setCachingAllowed(boolean cachingAllowed) {
        this.cachingAllowed = cachingAllowed;
    }

    public enum RunPhase {
	    BEFORE_CACHING, AFTER_CACHING, BOTH
    }
}

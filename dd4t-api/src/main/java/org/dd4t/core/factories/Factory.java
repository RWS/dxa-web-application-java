package org.dd4t.core.factories;

import org.dd4t.core.processors.Processor;
import org.dd4t.providers.CacheProvider;

import java.util.List;

/**
 * dd4t-2: Filters renamed to Processors
 */
public interface Factory {

    /**
     * @return list of Processors
     */
    public List<Processor> getProcessors ();

    /**
     * @param processors list of Processors
     */
    public void setProcessors (List<Processor> processors);



    public void setCacheProvider(CacheProvider cacheAgent);
}

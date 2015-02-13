package org.dd4t.core.factories.impl;

import org.dd4t.contentmodel.Binary;
import org.dd4t.contentmodel.Item;
import org.dd4t.core.exceptions.ProcessorException;
import org.dd4t.core.processors.LinkResolverProcessor;
import org.dd4t.core.processors.Processor;
import org.dd4t.core.processors.RunPhase;
import org.dd4t.providers.PayloadCacheProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all factories. All factories have a list of processors and a
 * default cache agent.
 *
 * @author bjornl, rai
 */
public abstract class BaseFactory {

    protected PayloadCacheProvider cacheProvider;
    private List<Processor> processors;

    public List<Processor> getProcessors () {
        if (processors == null) {
            this.processors = new ArrayList<>();
        }
        return processors;
    }

    /**
     * Configure through Spring
     * @param processors list of Processors to run
     */
    public void setProcessors (List<Processor> processors) {
        this.processors = new ArrayList<>();

        for (Processor processor : processors) {
            this.processors.add(processor);
        }
    }

    /**
     * Runs all the processors on an item. If the cachingAllowed is true it will
     * only run the processors where the result is allowed to be cached.
     *
     * @param item The DD4T Item
     * @throws org.dd4t.core.exceptions.ProcessorException
     */
    public void executeProcessors (Item item, RunPhase runPhase) throws ProcessorException {
        if (item != null) {
            for (Processor processor : getProcessors()) {
                if (runPhase == processor.getRunPhase()) {
                    this.execute(processor, item);
                }
            }
        }
    }

    private void execute (Processor processor, Item item) throws ProcessorException {
        // link resolving is not needed for the simple objects or binary
        if (processor instanceof LinkResolverProcessor && item instanceof Binary) {
            return;
        }
        processor.execute(item);
    }

    /**
     * Set the cache agent.
     */
    public void setCacheProvider(PayloadCacheProvider cacheAgent) {
        cacheProvider = cacheAgent;
    }


}
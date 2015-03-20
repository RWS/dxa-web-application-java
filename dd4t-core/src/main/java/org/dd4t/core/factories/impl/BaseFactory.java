package org.dd4t.core.factories.impl;

import org.dd4t.contentmodel.Item;
import org.dd4t.core.exceptions.ProcessorException;
import org.dd4t.core.processors.Processor;
import org.dd4t.core.processors.RunPhase;
import org.dd4t.core.request.AbstractRequestContext;
import org.dd4t.core.request.RequestContext;
import org.dd4t.core.util.HttpUtils;
import org.dd4t.providers.PayloadCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all factories. All factories have a list of processors and a
 * default cache agent.
 *
 * @author bjornl, rai
 */
public abstract class BaseFactory {
	private static final Logger LOG = LoggerFactory.getLogger(BaseFactory.class);
    protected PayloadCacheProvider cacheProvider;
    private List<Processor> processors;
	private Class requestClass;

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
    public void executeProcessors (Item item, RunPhase runPhase, RequestContext context) throws ProcessorException {
        if (item != null) {
            for (Processor processor : getProcessors()) {
                if (runPhase == processor.getRunPhase()) {
                    this.execute(processor, item, context);
                }
            }
        }
    }

    private void execute (Processor processor, Item item, RequestContext context) throws ProcessorException {
        processor.execute(item,context);
    }

    /**
     * Set the cache agent.
     */
    public void setCacheProvider(PayloadCacheProvider cacheAgent) {
        cacheProvider = cacheAgent;
    }

	// TODO: only HttpServletRequest supported for now.
	// TODO: configure a concrete Class through Spring
	protected RequestContext getRequestContext() {
		if (RequestContext.class.isAssignableFrom(requestClass) && AbstractRequestContext.class.isAssignableFrom(requestClass)) {
			try {
				return (RequestContext) requestClass.getDeclaredConstructor(Object.class).newInstance(HttpUtils.getCurrentRequest());
			} catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
				LOG.error(e.getLocalizedMessage(), e);
			}
		}
		LOG.error("Class {} does not extend from AbstractRequestContext!", requestClass.getCanonicalName());
		return null;
	}
}
/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.core.factories.impl;

import org.dd4t.contentmodel.Item;
import org.dd4t.core.exceptions.ProcessorException;
import org.dd4t.core.processors.Processor;
import org.dd4t.core.processors.RunPhase;
import org.dd4t.core.request.RequestContext;
import org.dd4t.core.util.HttpRequestContext;
import org.dd4t.providers.PayloadCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected List<Processor> processors;
    private Class requestContextClass;

    public List<Processor> getProcessors () {
        if (processors == null) {
            this.processors = new ArrayList<>();
        }
        return processors;
    }

    /**
     * Configure through Spring
     *
     * @param processors list of Processors to run
     */
    public void setProcessors (List<Processor> processors) {
        this.processors = new ArrayList<>();

        for (Processor processor : processors) {
            this.processors.add(processor);
        }
    }

    /**
     * Runs all the processors on an item. If cachingAllowed is true it will
     * only run the processors where the result is allowed to be cached.
     *
     * @param item The DD4T Item
     * @throws org.dd4t.core.exceptions.ProcessorException
     */
    public void executeProcessors (Item item, RunPhase runPhase, RequestContext context) throws ProcessorException {
        if (item != null) {
            for (Processor processor : getProcessors()) {
                if (runPhase == processor.getRunPhase() || processor.getRunPhase() == RunPhase.BOTH) {
                    this.execute(processor, item, context);
                }
            }
        }
    }

    private void execute (Processor processor, Item item, RequestContext context) throws ProcessorException {
        processor.execute(item, context);
    }

    /**
     * Set the cache agent.
     */
    public void setCacheProvider (PayloadCacheProvider cacheAgent) {
        cacheProvider = cacheAgent;
    }

    protected RequestContext getRequestContext () {

        if (requestContextClass == null) {
            requestContextClass = HttpRequestContext.class;
        }

        if (RequestContext.class.isAssignableFrom(requestContextClass)) {
            try {
                return (RequestContext) requestContextClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        LOG.error("Class {} does not extend from AbstractRequestContext!", requestContextClass.getCanonicalName());
        return null;
    }

    public void setRequestContextClass (final Class requestContextClass) {
        this.requestContextClass = requestContextClass;
    }
}
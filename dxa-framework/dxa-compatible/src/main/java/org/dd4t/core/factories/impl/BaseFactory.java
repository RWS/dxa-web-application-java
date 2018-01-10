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
import org.dd4t.core.databind.DataBinder;
import org.dd4t.core.exceptions.ProcessorException;
import org.dd4t.core.processors.Processor;
import org.dd4t.core.processors.RunPhase;
import org.dd4t.core.request.RequestContext;
import org.dd4t.providers.PayloadCacheProvider;

import javax.annotation.Resource;
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
    protected List<Processor> processors;

    @Resource
    protected List<DataBinder> dataBinders;


    public List<Processor> getProcessors() {
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
    public void setProcessors(List<Processor> processors) {
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

    public void executeProcessors(Item item, RunPhase runPhase, RequestContext context) throws ProcessorException {
        if (item != null) {
            for (Processor processor : getProcessors()) {
                if (runPhase == processor.getRunPhase() || processor.getRunPhase() == RunPhase.BOTH) {
                    this.execute(processor, item, context);
                }
            }
        }
    }


    /**
     * Method finds the relevant databinder for given source by calling canDeserialize() on them.
     *
     * @param source
     * @return
     */
    protected DataBinder selectDataBinder(final String source) {
        if (dataBinders == null || dataBinders.isEmpty()) {
            return null;
        }

        if (dataBinders.size() == 1) {
            return dataBinders.get(0);
        }

        for (DataBinder binder : dataBinders) {
            if (binder.canDeserialize(source)) {
                return binder;
            }
        }

        return null;
    }


    public List<DataBinder> getDataBinders() {
        return dataBinders;
    }

    public void setDataBinders(List<DataBinder> dataBinder) {
        this.dataBinders = dataBinder;
    }

    private void execute(Processor processor, Item item, RequestContext context) throws ProcessorException {
        processor.execute(item, context);
    }

    /**
     * Set the cache agent.
     */
    public void setCacheProvider(PayloadCacheProvider cacheAgent) {
        cacheProvider = cacheAgent;
    }
}
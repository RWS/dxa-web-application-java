package org.dd4t.core.processors;

import org.dd4t.contentmodel.Item;
import org.dd4t.core.exceptions.ProcessorException;
import org.dd4t.core.request.RequestContext;

public interface Processor {

    /**
     * Execute the Processor on the Factory Item
     *
     * @param item the Tridion item
     * @throws org.dd4t.core.exceptions.ProcessorException
     */
	public void execute(Item item, RequestContext requestContext) throws ProcessorException;

    public void setRunPhase(RunPhase phase);

    public RunPhase getRunPhase();
}
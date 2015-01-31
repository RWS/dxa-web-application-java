package org.dd4t.core.processors;

import org.dd4t.contentmodel.Item;
import org.dd4t.core.exceptions.ProcessorException;

public interface Processor {

    /**
     * Execute the Processor on the Factory Item
     *
     * @param item the Tridion item
     * @throws org.dd4t.core.exceptions.ProcessorException
     */
    public void execute(Item item) throws ProcessorException;

    public void setRunPhase(RunPhase phase);

    public RunPhase getRunPhase();
}
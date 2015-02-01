package org.dd4t.core.processors.impl;

import org.dd4t.contentmodel.*;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.factories.impl.ComponentPresentationFactoryImpl;
import org.dd4t.core.processors.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pagefactory processor intended to resolve DCP's on pages at the factory level. It checks the page
 * being produced, finds the dynamic components (if any), and resolves these components through
 * the ComponentFactory.
 *
 * @author Rogier Oudshoorn, Raimond Kempees
 */
public class DcpResolver extends BaseProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(DcpResolver.class);

    @Override
    public void execute(Item item) {
        LOG.debug("Processing item: {} ", item);
        if (item instanceof Page) {
            final Page page = (Page) item;

            LOG.debug("DCP Resolver detected {} component presentations.", page.getComponentPresentations().size());

            for (ComponentPresentation cp : page.getComponentPresentations()) {
                if (cp.isDynamic()) {
                    LOG.debug("Detected dynamic component presentation " + cp);

                    try {
                        final ComponentPresentation componentPresentation = ComponentPresentationFactoryImpl.getInstance().getComponentPresentation(cp.getComponent().getId(), cp.getComponentTemplate().getId());
                        cp.setComponent(componentPresentation.getComponent());
	                    cp.setViewModel(componentPresentation.getAllViewModels());
                    } catch (FactoryException e) {
                        LOG.error("Unable to find component by id " + cp.getComponent().getId(), e);
                    }
                }
            }
        }
    }
}

package org.dd4t.core.filters.impl;

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.Item;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.core.factories.impl.ComponentFactoryImpl;
import org.dd4t.core.filters.Filter;
import org.dd4t.core.filters.FilterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

/**
 * Pagefactory filter intended to resolve DCP's on pages at the factory level. It checks the page
 * being produced, finds the dynamic components (if any), and resolves these components through
 * the ComponentFactory.
 *
 * @author Rogier Oudshoorn, Raimond Kempees
 */
public class HybridPublishingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(HybridPublishingFilter.class);

    @Override
    public void doFilter(Item item) throws FilterException, SerializationException {
        LOG.debug("[HybridPublishingFilter] acting upon item " + item);

        // filter only acts on pages
        if (item instanceof GenericPage) {
            GenericPage page = (GenericPage) item;

            LOG.debug("[HybridPublishingFilter] Detected " + page.getComponentPresentations().size() + " component presentations.");

            for (ComponentPresentation cp : page.getComponentPresentations()) {
                if (cp.isDynamic()) {
                    LOG.debug("[HybridPublishingFilter] Detected dynamic component presentation " + cp);

                    try {
                        // retrieve the dynamic component based on template
                        GenericComponent comp = ComponentFactoryImpl.getInstance().getComponent(cp.getComponent().getId(), cp.getComponentTemplate().getId());

                        // set the dynamic component
                        cp.setComponent(comp);
                    } catch (ItemNotFoundException | ParseException ex) {
                        // note: the other exceptions (authorization & authentication) are passed on
                        LOG.error("Unable to find component by id " + cp.getComponent().getId(), ex);
                    }
                }
            }
        }
        LOG.debug("[HybridPublishingFilter] exits for item " + item);
    }

    // TODO
    @Override
    public boolean getCachingAllowed() {
        return false;
    }

    @Override
    public void setCachingAllowed(boolean arg0) {

    }

}

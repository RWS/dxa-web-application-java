package com.sdl.webapp.tridion;

import com.tridion.dcp.ComponentPresentationFactory;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
/**
 * <p>BrokerComponentPresentationProvider class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class BrokerComponentPresentationProvider extends AbstractBrokerComponentPresentationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerComponentPresentationProvider.class);
    private static final Map<Integer, ComponentPresentationFactory> FACTORY_CACHE = new ConcurrentHashMap<>();
    private static final String ERROR_MESSAGE = "Component Presentation not found for componentId: %d, templateId: %d and publicationId: %d";

    /**
     * {@inheritDoc}
     */
    @Override
    protected ComponentPresentation getDynamicComponentPresentationInternal(int componentId, int templateId, int publicationId) throws ItemNotFoundException, SerializationException {
        ComponentPresentationFactory factory = FACTORY_CACHE.get(publicationId);
        if (factory == null) {
            factory = new ComponentPresentationFactory(publicationId);
            FACTORY_CACHE.put(publicationId, factory);
        }

        com.tridion.dcp.ComponentPresentation result = templateId != 0 ?
                factory.getComponentPresentation(componentId, templateId) :
                factory.getComponentPresentationWithHighestPriority(componentId);

        if (result == null) {
            LOG.info(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
            throw new ItemNotFoundException(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
        }

        return getComponentPresentation(result.getContent());
    }

}

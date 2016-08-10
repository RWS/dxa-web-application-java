package com.sdl.webapp.tridion;


import com.sdl.web.api.broker.WebComponentPresentationFactoryImpl;
import com.sdl.web.api.dynamic.WebComponentPresentationFactory;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class BrokerComponentPresentationProvider extends AbstractBrokerComponentPresentationProvider {

    private static final Map<Integer, WebComponentPresentationFactory> FACTORY_CACHE = new ConcurrentHashMap<>();
    private static final String ERROR_MESSAGE = "Component Presentation not found for componentId: %d, templateId: %d and publicationId: %d";

    /**
     * {@inheritDoc}
     */
    @Override
    protected ComponentPresentation getDynamicComponentPresentationInternal(int componentId, int templateId, int publicationId) throws ItemNotFoundException, SerializationException {
        WebComponentPresentationFactory factory = FACTORY_CACHE.get(publicationId);
        if (factory == null) {
            factory = new WebComponentPresentationFactoryImpl(publicationId);
            FACTORY_CACHE.put(publicationId, factory);
        }

        com.tridion.dcp.ComponentPresentation result = templateId != 0 ?
                factory.getComponentPresentation(componentId, templateId) :
                factory.getComponentPresentationWithHighestPriority(componentId);

        if (result == null) {
            log.info(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
            throw new ItemNotFoundException(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
        }

        return getComponentPresentation(result.getContent());
    }

}

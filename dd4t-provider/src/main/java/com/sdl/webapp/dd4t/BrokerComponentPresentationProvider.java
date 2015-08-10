package com.sdl.webapp.dd4t;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.tridion.dcp.ComponentPresentationFactory;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.impl.ComponentPresentationImpl;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.providers.BaseBrokerProvider;
import org.dd4t.databind.builder.json.JsonDataBinder;
import org.dd4t.providers.ComponentPresentationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BrokerComponentPresentationProvider
 * - Temporary override of the standard DD4T DCP provider to correct the DCP implementation.
 **
 * @author nic
 */
public class BrokerComponentPresentationProvider extends BaseBrokerProvider implements ComponentPresentationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerComponentPresentationProvider.class);

    private static final Map<Integer, ComponentPresentationFactory> FACTORY_CACHE = new ConcurrentHashMap<>();
    private static final String ERROR_MESSAGE = "Component Presentation not found for componentId: %d, templateId: %d and publicationId: %d";

    private Class<? extends org.dd4t.contentmodel.ComponentPresentation> concreteComponentPresentation;
    private Class<? extends ComponentTemplate> concreteComponentTemplateImpl;

    /**
     * Retrieves content of a Dynamic Component Presentation by looking up its componentId and publicationId.
     * A templateId is not provided, so the DCP with the highest linking priority is retrieved.
     * <p/>
     * <b>Note: This method performs significantly slower than getDynamicComponentPresentation(int, int, int)!
     * Do provide a templateId!</b>
     *
     * @param componentId   int representing the Component item id
     * @param publicationId int representing the Publication id of the DCP
     * @return String representing the content of the DCP
     * @throws ItemNotFoundException if the requested DCP cannot be found
     */
    @Override public org.dd4t.contentmodel.ComponentPresentation getDynamicComponentPresentation (int componentId, int publicationId) throws ItemNotFoundException, SerializationException {
        return getDynamicComponentPresentation(componentId, 0, publicationId);
    }

    /**
     * Retrieves content of a Dynamic Component Presentation by looking up its componentId, templateId and publicationId.
     *
     * @param componentId   int representing the Component item id
     * @param templateId    int representing the Component Template item id
     * @param publicationId int representing the Publication id of the DCP
     * @return String representing the content of the DCP
     * @throws ItemNotFoundException if the requested DCP cannot be found
     */
    @Override
    public ComponentPresentation getDynamicComponentPresentation(int componentId, int templateId, int publicationId) throws ItemNotFoundException, SerializationException {
        ComponentPresentationFactory factory = FACTORY_CACHE.get(publicationId);
        if (factory == null) {
            factory = new ComponentPresentationFactory(publicationId);
            FACTORY_CACHE.put(publicationId, factory);
        }

        com.tridion.dcp.ComponentPresentation result;
        String resultString;
        if (templateId != 0) {
            result = factory.getComponentPresentation(componentId, templateId);

            if (result == null) {
                LOG.info(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
                throw new ItemNotFoundException(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
            }

            resultString = result.getContent();
        } else {
            result = factory.getComponentPresentationWithHighestPriority(componentId);
            if (result == null) {
                LOG.info(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
                throw new ItemNotFoundException(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
            }
            resultString = result.getContent();
        }

        if (!StringUtils.isEmpty(resultString)) {

            try {
                // TODO: Use a more generic way of deserializing the DCP
                // This can not be used as the default JSON factory does not do: addMixInAnnotations(Field.class, BaseFieldMixIn.class);
                //
                //return SerializerFactory.deserialize(decodeAndDecompressContent(resultString), ComponentPresentationImpl.class);

                JsonNode parsedResult = JsonDataBinder.getGenericMapper().readTree(decodeAndDecompressContent(resultString));
                final JsonParser parser = parsedResult.traverse();
                return JsonDataBinder.getGenericMapper().readValue(parser, ComponentPresentationImpl.class);

                // TODO: Make this more error robust? So it can handle components as well?

            } catch (IOException e) {
                LOG.error("Could not parse component presentation", e);
            }
        }
        return null;
    }

    /**
     * Convenience method to obtain a list of component presentations for the same template id.
     * <p/>
     * TODO
     *
     * @param itemUris      array of found Component TCM IDs
     * @param templateId    the CT Id to fetch DCPs on
     * @param publicationId the current Publication Id
     * @return a List of Component Presentations
     * @throws org.dd4t.core.exceptions.ItemNotFoundException
     * @throws org.dd4t.core.exceptions.SerializationException
     */
    @Override public List<ComponentPresentation> getDynamicComponentPresentations (final String[] itemUris, final int templateId, final int publicationId) throws ItemNotFoundException, SerializationException {
        return new ArrayList<>();
    }

    public void setConcreteComponentPresentation (final Class<? extends org.dd4t.contentmodel.ComponentPresentation> concreteComponentPresentation) {
        this.concreteComponentPresentation = concreteComponentPresentation;
    }

    public void setConcreteComponentTemplateImpl (final Class<? extends ComponentTemplate> concreteComponentTemplateImpl) {
        this.concreteComponentTemplateImpl = concreteComponentTemplateImpl;
    }
}

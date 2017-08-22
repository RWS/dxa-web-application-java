package com.sdl.webapp.tridion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.tridion.dcp.ComponentPresentationFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.impl.ComponentPresentationImpl;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.databind.builder.json.JsonDataBinder;
import org.dd4t.providers.AbstractComponentPresentationProvider;
import org.dd4t.providers.ComponentPresentationProvider;
import org.dd4t.providers.ComponentPresentationResultItem;
import org.dd4t.providers.ComponentPresentationResultItemImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BrokerComponentPresentationProvider temporary overrides of the standard DD4T DCP provider to correct the DCP implementation.
 */
@Service
@Slf4j
public class BrokerComponentPresentationProvider extends AbstractComponentPresentationProvider implements ComponentPresentationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerComponentPresentationProvider.class);

    private static final Map<Integer, ComponentPresentationFactory> FACTORY_CACHE = new ConcurrentHashMap<>();

    private static final String ERROR_MESSAGE = "Component Presentation not found for componentId: %d, templateId: %d and publicationId: %d";

    /**
     * {@inheritDoc}
     */
    protected String getDynamicComponentPresentationInternal(int componentId, int templateId, int publicationId) throws ItemNotFoundException, SerializationException {
        ComponentPresentationFactory factory = FACTORY_CACHE.get(publicationId);
        if (factory == null) {
            factory = new ComponentPresentationFactory(publicationId);
            FACTORY_CACHE.put(publicationId, factory);
        }

        com.tridion.dcp.ComponentPresentation result = templateId != 0 ?
                factory.getComponentPresentation(componentId, templateId) :
                factory.getComponentPresentationWithHighestPriority(componentId);

        if (result == null) {
            log.info(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
            throw new ItemNotFoundException(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
        }

        return result.getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDynamicComponentPresentation(int componentId, int publicationId)
            throws ItemNotFoundException, SerializationException {
        return getDynamicComponentPresentation(componentId, 0, publicationId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDynamicComponentPresentation(int componentId, int templateId, int publicationId)
            throws ItemNotFoundException, SerializationException {
        return getDynamicComponentPresentationInternal(componentId, templateId, publicationId);
    }

    @Override
    public ComponentPresentationResultItem<String> getDynamicComponentPresentationItem (int componentId, int publicationId) throws ItemNotFoundException, SerializationException{
        return getDynamicComponentPresentationItem(componentId, 0, publicationId);
    }

    @Override
    public ComponentPresentationResultItem<String> getDynamicComponentPresentationItem (int componentId, int templateId, int publicationId) throws ItemNotFoundException, SerializationException{
        ComponentPresentationFactory factory = FACTORY_CACHE.get(publicationId);

        if (factory == null) {
            factory = new ComponentPresentationFactory(publicationId);
            FACTORY_CACHE.put(publicationId, factory);
        }

        com.tridion.dcp.ComponentPresentation result;
        String resultString;
        ComponentPresentationResultItemImpl resultmodel;

        if (templateId != 0) {
            result = factory.getComponentPresentation(componentId, templateId);
        } else {
            result = factory.getComponentPresentationWithHighestPriority(componentId);
        }

        if(result != null){
            resultmodel = new ComponentPresentationResultItemImpl(result.getPublicationId(), result.getComponentId(), result.getComponentTemplateId());

            assertQueryResultNotNull(result,componentId,templateId,publicationId);
            resultString = result.getContent();

            if (!StringUtils.isEmpty(resultString)) {
                resultmodel.setContentSource(decodeAndDecompressContent(resultString));
            }
            else{
                resultmodel.setContentSource(resultString);
            }
        }
        else{
            resultmodel = new ComponentPresentationResultItemImpl(0, 0, 0);
        }

        return resultmodel;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDynamicComponentPresentations(final String[] itemUris, final int templateId, final int publicationId)
            throws ItemNotFoundException, SerializationException {
        return Collections.emptyList();
    }

    /**
     * <p>getComponentPresentation.</p>
     *
     * @param dcpComponentPresentationRaw a {@link java.lang.String} object.
     * @return a {@link org.dd4t.contentmodel.ComponentPresentation} object.
     * @throws org.dd4t.core.exceptions.SerializationException if any.
     */
    protected ComponentPresentation getComponentPresentation(String dcpComponentPresentationRaw) throws SerializationException {
        if (!StringUtils.isEmpty(dcpComponentPresentationRaw)) {
            try {
                // TODO: Use a more generic way of deserializing the DCP
                // This can not be used as the default JSON factory does not do: addMixInAnnotations(Field.class, BaseFieldMixIn.class);

                JsonNode parsedResult = JsonDataBinder.getGenericMapper().readTree(decodeAndDecompressContent(dcpComponentPresentationRaw));
                final JsonParser parser = parsedResult.traverse();
                return JsonDataBinder.getGenericMapper().readValue(parser, ComponentPresentationImpl.class);

                // TODO: Make this more error robust? So it can handle components as well?

            } catch (IOException e) {
                LOG.error("Could not parse component presentation", e);
            }
        }
        return null;
    }
}

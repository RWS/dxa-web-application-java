package com.sdl.webapp.tridion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.impl.ComponentPresentationImpl;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.providers.BaseBrokerProvider;
import org.dd4t.databind.builder.json.JsonDataBinder;
import org.dd4t.providers.ComponentPresentationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * AbstractBrokerComponentPresentationProvider temporary overrides of the standard DD4T DCP provider to correct the DCP implementation.
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public abstract class AbstractBrokerComponentPresentationProvider extends BaseBrokerProvider implements ComponentPresentationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractBrokerComponentPresentationProvider.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentPresentation getDynamicComponentPresentation(int componentId, int publicationId)
            throws ItemNotFoundException, SerializationException {
        return getDynamicComponentPresentation(componentId, 0, publicationId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentPresentation getDynamicComponentPresentation(int componentId, int templateId, int publicationId)
            throws ItemNotFoundException, SerializationException {
        return getDynamicComponentPresentationInternal(componentId, templateId, publicationId);
    }

    /** {@inheritDoc} */
    @Override
    public List<ComponentPresentation> getDynamicComponentPresentations(final String[] itemUris, final int templateId, final int publicationId)
            throws ItemNotFoundException, SerializationException {
        return Collections.emptyList();
    }

    /**
     * <p>getDynamicComponentPresentationInternal.</p>
     *
     * @param componentId a int.
     * @param templateId a int.
     * @param publicationId a int.
     * @return a {@link org.dd4t.contentmodel.ComponentPresentation} object.
     * @throws org.dd4t.core.exceptions.ItemNotFoundException if any.
     * @throws org.dd4t.core.exceptions.SerializationException if any.
     */
    protected abstract ComponentPresentation getDynamicComponentPresentationInternal(int componentId, int templateId, int publicationId)
            throws ItemNotFoundException, SerializationException;

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

package org.dd4t.core.factories.impl;

import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.impl.ComponentImpl;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.factories.ComponentFactory;
import org.dd4t.core.services.LabelService;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.ComponentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.ParseException;

public class ComponentFactoryImpl extends BaseFactory implements ComponentFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentFactoryImpl.class);

    private static final ComponentFactoryImpl INSTANCE = new ComponentFactoryImpl();
    @Autowired
    protected ComponentProvider componentProvider;
    @Autowired
    private LabelService labelService;

    private ComponentFactoryImpl() {
        LOG.debug("Create new instance");
    }

    public static ComponentFactoryImpl getInstance() {
        return INSTANCE;
    }

    /**
     * Get the component by the component uri and template uri.
     * but NO CT rendering (so from the Dynamic Template Tab) should take place,
     * as this is done in a View
     *
     * Null values should be handled in the controller
     *
     * The Content Service should use ComponentPresentationFactory.getComponentPresentation(int publicationId, int componentId, int templateId)
     * OR
     * ComponentPresentationAssembler.getContent(int componentId, int componentTemplateId)
     * if we want to use REL linking. :)
     * Metadata should be added into the DD4T stack @ publishtime
     * Rendered output is cached in output cache
     *
     * @return the component
     * @throws org.dd4t.core.exceptions.FactoryException if no item found NotAuthorizedException if the user is not authorized to get the component
     */
    @Override
    public GenericComponent getComponent(String componentURI, String viewOrTemplateURI) throws FactoryException {
        LOG.debug("Enter getComponent with componentURI: {} and templateURI: {}", componentURI, viewOrTemplateURI);

        if (viewOrTemplateURI == null || viewOrTemplateURI.length() == 0) {
            throw new FactoryException("Provide a CT view or TCMURI");
        }

	    TCMURI componentTcmUri;

	    TCMURI templateTcmUri;
	    try {
		    componentTcmUri = new TCMURI(componentURI);
		    String templateURI = labelService.getViewLabel(viewOrTemplateURI);
		    templateTcmUri = new TCMURI(templateURI);
	    }
	    catch (ParseException | IOException e)
	    {
		    throw new FactoryException(e);
	    }
        componentURI = componentTcmUri.toString();
        int publicationId = componentTcmUri.getPublicationId();
        int componentId = componentTcmUri.getItemId();
        int templateId = templateTcmUri.getItemId();

        String key = getKey(publicationId, componentId, templateId);
        CacheElement<GenericComponent> cacheElement = cacheProvider.loadFromLocalCache(key);
        GenericComponent component;

        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);

                    try {
                        String componentModel = componentProvider.getDynamicComponentPresentation(componentId, templateId, publicationId);
                        component = deserialize(componentModel, ComponentImpl.class);
                    } catch (ItemNotFoundException | SerializationException e) {
                        cacheElement.setPayload(null);
                        cacheProvider.storeInItemCache(key, cacheElement);
                        throw new FactoryException(e);
                    }

                    cacheElement.setPayload(component);
                    cacheProvider.storeInItemCache(key, cacheElement, publicationId, componentId);
                    LOG.debug("Added component with uri: {} and template: {} to cache", componentURI, viewOrTemplateURI);
                } else {
                    LOG.debug("Return component for componentURI: {} and templateURI: {} from cache", componentURI, viewOrTemplateURI);
                    component = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Return component for componentURI: {} and templateURI: {} from cache", componentURI, viewOrTemplateURI);
            component = cacheElement.getPayload();
        }

        LOG.debug("Exit getComponent");
        return component;
    }

    private String getKey(int publicationId, int componentId, int templateId) {
        return String.format("Component-%d-%d-%d", publicationId, componentId, templateId);
    }

    public ComponentProvider getComponentProvider() {
        return componentProvider;
    }

    public void setComponentProvider(ComponentProvider componentProvider) {
        this.componentProvider = componentProvider;
    }

    public LabelService getLabelService() {
        return labelService;
    }

    public void setLabelService(LabelService labelService) {
        this.labelService = labelService;
    }
}

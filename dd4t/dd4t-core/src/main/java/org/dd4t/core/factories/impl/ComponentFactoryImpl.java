package org.dd4t.core.factories.impl;

import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.contentmodel.impl.ComponentImpl;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.factories.ComponentFactory;
import org.dd4t.core.filters.FilterException;
import org.dd4t.core.request.RequestContext;
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
    // Singleton
    private static final ComponentFactoryImpl _instance = new ComponentFactoryImpl();
    @Autowired
    protected ComponentProvider componentProvider;
//    @Autowired
//    private LabelService labelService;

    private ComponentFactoryImpl() {
        LOG.debug("Create new instance");
    }

    public static ComponentFactoryImpl getInstance() {
        return _instance;
    }

    /**
     * Get a component by its uri and Component Template URI.
     *
     * @param componentURI      String representing the Component TCMURI to retrieve
     * @param viewOrTemplateURI String representing either the View Name or Component Template TCMURI
     *                          to use when looking up the DCP
     * @return
     * @throws ItemNotFoundException
     */
    @Override
    public GenericComponent getComponent(String componentURI, String viewOrTemplateURI) throws ItemNotFoundException, ParseException, FilterException, SerializationException {
        return getComponent(componentURI, viewOrTemplateURI, null);
    }

    /**
     * Get the component by the component uri and template uri.
     * TODO: Component Should be Fetched based on comp uri and CT uri,
     * but NO CT rendering (so from the Dynamic Template Tab) should take place,
     * as this is done in a View
     * Hammerfest should use ComponentPresentationFactory.getComponentPresentation(int publicationId, int componentId, int templateId)
     * OR
     * ComponentPresentationAssembler.getContent(int componentId, int componentTemplateId)
     * if we want to use REL linking. :)
     * Metadata should be added into the DD4T stack @ publishtime
     * TODO: note on the proper request flows (eg. the Component XML is cached here.
     * Rendered output is cached in output cache
     *
     * @return the component
     * @throws ItemNotFoundException if no item found NotAuthorizedException if the user is not authorized to get the component
     */
    @Override
    public GenericComponent getComponent(String componentURI, String viewOrTemplateURI, RequestContext context)
            throws ItemNotFoundException, ParseException, FilterException, SerializationException {
        LOG.debug("Enter getComponent with componentURI: {} and templateURI: {}", componentURI, viewOrTemplateURI);

        if (viewOrTemplateURI == null || viewOrTemplateURI.length() == 0) {
            throw new ItemNotFoundException("Provide a CT view or TCMURI");
        }

        TCMURI componentTcmUri = new TCMURI(componentURI);
        componentURI = componentTcmUri.toString();
        int publicationId = componentTcmUri.getPublicationId();
        int componentId = componentTcmUri.getItemId();
        int templateId;
        TCMURI templateTcmUri;


            String templateURI = "tcm:0-0-0";
            templateTcmUri = new TCMURI(templateURI);
            templateId = templateTcmUri.getItemId();


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
                    } catch (ItemNotFoundException infe) {
                        cacheElement.setPayload(null);
                        cacheProvider.storeInItemCache(key, cacheElement);
                        throw infe;
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

        if (component == null) {
            throw new ItemNotFoundException("Cannot find Component model for uri: " + componentURI);
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
}

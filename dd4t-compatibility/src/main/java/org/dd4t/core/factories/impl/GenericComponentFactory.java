/**
 * Copyright 2011 Capgemini & SDL
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dd4t.core.factories.impl;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.NotAuthenticatedException;
import org.dd4t.contentmodel.exceptions.NotAuthorizedException;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.factories.ComponentFactory;
import org.dd4t.core.request.RequestContext;
import org.dd4t.providers.PayloadCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backwards compatibility class
 * @author Rogier Oudshoorn
 *
 */
public class GenericComponentFactory extends BaseFactory implements ComponentFactory {

    private static final Logger LOG = LoggerFactory.getLogger(GenericComponentFactory.class);

    @Override
    public void setCacheProvider (PayloadCacheProvider cacheAgent) {
        // necessary setter, but don't bother
    }

    @Override
    public Component getComponent (String uri) throws ItemNotFoundException, NotAuthorizedException, NotAuthenticatedException {
        ComponentPresentation cp;
        try {
            cp = ComponentPresentationFactoryImpl.getInstance().getComponentPresentation(uri, null);
        } catch (FactoryException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return null;
        }

        return cp.getComponent();
    }

    @Override
    public Component getComponent (String uri, RequestContext context) throws ItemNotFoundException, NotAuthorizedException, NotAuthenticatedException {
        return getComponent(uri);
    }

    @Override
    public Component getComponent (String componentUri, String componentTemplateUri) throws ItemNotFoundException, NotAuthorizedException, NotAuthenticatedException {
        ComponentPresentation cp;
        try {
            cp = ComponentPresentationFactoryImpl.getInstance().getComponentPresentation(componentUri, componentTemplateUri);
        } catch (FactoryException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return null;
        }

        return cp.getComponent();
    }

    @Override
    public Component getComponent (String componentUri, String componentTemplateUri, RequestContext context) throws ItemNotFoundException, NotAuthorizedException, NotAuthenticatedException {
        return getComponent(componentUri, componentTemplateUri);
    }

    @Override
    public Component getEmbeddedComponent (String uri) throws ItemNotFoundException {
        // Discuss if this is somehting we still should support, we probably do ...

        return null;
    }


}

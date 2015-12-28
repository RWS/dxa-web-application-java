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
package org.dd4t.core.factories;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.NotAuthenticatedException;
import org.dd4t.contentmodel.exceptions.NotAuthorizedException;
import org.dd4t.core.request.RequestContext;

@Deprecated
public interface ComponentFactory extends Factory {
    /**
     * Get a Component by its uri. No security available; the method will fail if a
     * SecurityFilter is configured on the factory.
     * @param uri
     * @param context
     * @return
     * @throws ItemNotFoundException
     * @throws NotAuthorizedException
     * @throws NotAuthenticatedException
     */
    Component getComponent (String uri) throws ItemNotFoundException, NotAuthorizedException, NotAuthenticatedException;

    /**
     * Get a Component by its uri. The request context is used by the security filter (if there is one).
     * @param uri
     * @param context
     * @return
     * @throws ItemNotFoundException
     * @throws NotAuthorizedException
     * @throws NotAuthenticatedException
     */
    Component getComponent (String uri, RequestContext context) throws ItemNotFoundException, NotAuthorizedException, NotAuthenticatedException;

    /**
     * Get a component by its uri and component template uri. No security available; the method will fail if a
     * SecurityFilter is configured on the factory.
     * @param componentUri
     * @param componentTemplateUri
     * @param context
     * @return
     * @throws ItemNotFoundException
     * @throws NotAuthorizedException
     * @throws NotAuthenticatedException
     */
    Component getComponent (String componentUri, String componentTemplateUri) throws ItemNotFoundException, NotAuthorizedException, NotAuthenticatedException;

    /**
     * Get a component by its uri and component template uri. The request context is used by the security filter (if there is one).
     * @param componentUri
     * @param componentTemplateUri
     * @param context
     * @return
     * @throws ItemNotFoundException
     * @throws NotAuthorizedException
     * @throws NotAuthenticatedException
     */
    Component getComponent (String componentUri, String componentTemplateUri, RequestContext context) throws ItemNotFoundException, NotAuthorizedException, NotAuthenticatedException;

    /**
     * Get an embedded (i.e.: stored inside of a DD4T page) component. This method is slightly slower as it has to utilize the componentlinker.
     *
     * @param tcmUri
     * @return
     * @throws ItemNotFoundException
     */
    Component getEmbeddedComponent (String uri) throws ItemNotFoundException;
}

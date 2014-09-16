/**  
 *  Copyright 2011 Capgemini & SDL
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dd4t.core.factories.impl;

import java.text.ParseException;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Schema;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.NotAuthenticatedException;
import org.dd4t.contentmodel.exceptions.NotAuthorizedException;
import org.dd4t.contentmodel.impl.PublicationImpl;
import org.dd4t.contentmodel.impl.SchemaImpl;
import org.dd4t.contentmodel.impl.SimpleComponentImpl;
import org.dd4t.core.factories.ComponentFactory;
import org.dd4t.core.filters.FilterException;
import org.dd4t.core.filters.impl.BaseFilter;
import org.dd4t.core.request.RequestContext;
import org.dd4t.core.util.TridionUtils;
import org.dd4t.providers.ComponentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import com.tridion.broker.StorageException;
import com.tridion.storage.ComponentMeta;
import com.tridion.util.TCMURI;

/**
 * Factory for SimpleComponent objects. Note the simple component objects should
 * not be cached because they are already cached in the broker.
 * 
 * @author bjornl, rooudsho
 * 
 */
public class SimpleComponentFactory extends BaseFactory implements
		ComponentFactory {

	private static Logger logger = LoggerFactory.getLogger(SimpleComponentFactory.class);

	// provider class to use for finding content
	private ComponentProvider componentProvider;


	/**
	 * Get the component by the uri
	 * 
	 * @return the component
	 * @throws ItemNotFoundException
	 *             if no item found
	 * @throws NotAuthorizedException
	 *             if the user is not authorized to get the item
	 * @throws NotAuthenticatedException 
	 */
	@Override
	public Component getComponent(String componentUri, RequestContext context)
			throws ItemNotFoundException, NotAuthorizedException, NotAuthenticatedException {

		return getComponent(componentUri, null, context);
	}

	/**
	 * Get the component by the uri. No security available; the method will fail if a
	 * SecurityFilter is configured on the factory.
	 * 
	 * @return the component
	 * @throws ItemNotFoundException
	 *             if no item found
	 * @throws NotAuthorizedException
	 *             if the user is not authorized to get the item
	 * @throws NotAuthenticatedException 
	 */
	@Override
	public Component getComponent(String uri) throws ItemNotFoundException,
			NotAuthorizedException, NotAuthenticatedException {

		return getComponent(uri, null, null);
	}

	/**
	 * Get the component by the component uri and component template uri
	 * 
	 * @return the component
	 * @throws ItemNotFoundException
	 *             if no item found
	 * @throws NotAuthorizedException
	 *             if the user is not authorized to get the item
	 * @throws NotAuthenticatedException 
	 */
	@Override
	public Component getComponent(String componentUri,
			String componentTemplateUri, RequestContext context)
			throws ItemNotFoundException, NotAuthorizedException, NotAuthenticatedException {

		if (context == null && securityFilterPresent()) {
			throw new RuntimeException(
					"use of getComponent is not allowed when a SecurityFilter is set");
		}		
		
		StopWatch stopWatch = null;
		if (logger.isDebugEnabled()) {
			logger.debug("Enter getComponent url: " + componentUri
					+ " and componentTemplateUri: " + componentTemplateUri);
			stopWatch = new StopWatch("getComponent");
			stopWatch.start();
		}

		Component component = null;

		try {
		        TCMURI tcmUri = new TCMURI(componentUri);
		        		                                                  
                ComponentMeta componentMeta = componentProvider.getComponentMeta(tcmUri.getItemId(), tcmUri.getPublicationId());
                
        		component = new SimpleComponentImpl();
        		component.setId(TridionUtils.createUri(componentMeta).toString());
        		component.setTitle(componentMeta.getTitle());
        		component.setNativeMetadata(componentMeta);

        		PublicationImpl pub = new PublicationImpl();
        		pub.setId(String.valueOf(componentMeta.getPublicationId()));
        		component.setPublication(pub);

        		Schema schema = new SchemaImpl();
        		schema.setId(String.valueOf(componentMeta.getSchemaId()));
        		component.setSchema(schema);
        		
                if(componentTemplateUri != null){
                	TCMURI ctUri = new TCMURI(componentTemplateUri);
                	
                	component.setSource(componentProvider.getComponentXMLByTemplate(tcmUri.getItemId(), ctUri.getItemId(), tcmUri.getItemId()));
                }
                else{
                	component.setSource(componentProvider.getComponentXML(tcmUri.getItemId(), tcmUri.getItemId()));
                }
                        

			try {
				doFilters(component, context, BaseFilter.RunPhase.Both);
			} catch (FilterException e) {
				logger.error("Error in filter. ", e);
				throw new RuntimeException(e);
			}

		} catch (ParseException e) {
			logger.error("Not able to parse uri: " + componentUri, e);
			throw new RuntimeException(e);
		} catch (StorageException e) {
			logger.error("Not possible to get uri: " + componentUri, e);
			throw new RuntimeException(e);
		}

		if (logger.isDebugEnabled()) {
			stopWatch.stop();
			logger.debug("Exit getComponent (" + stopWatch.getTotalTimeMillis()
					+ " ms)");
		}

		return component;
	}

	/**
	 * Get the component by the component uri and component template uri. No
	 * security available; the method will fail if a SecurityFilter is
	 * configured on the factory.
	 * 
	 * @return the component
	 * @throws ItemNotFoundException
	 *             if no item found
	 * @throws NotAuthorizedException
	 *             if the user is not authorized to get the item
	 * @throws NotAuthenticatedException 
	 */
	@Override
	public Component getComponent(String componentUri,
			String componentTemplateUri) throws ItemNotFoundException,
			NotAuthorizedException, NotAuthenticatedException {

		return getComponent(componentUri, componentTemplateUri, null);
	}

	public ComponentProvider getComponentProvider() {
		return componentProvider;
	}

	public void setComponentProvider(ComponentProvider brokerComponentProvider) {
		this.componentProvider = brokerComponentProvider;
	}

	@Override
	public Component getEmbeddedComponent(String uri)
			throws ItemNotFoundException {
		throw new RuntimeException("This method is not suppored by the SimpleComponentFactory");
	}
}
	
	
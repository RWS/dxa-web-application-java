/**  
 *  Copyright 2013 Nordea
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
package org.dd4t.core.filters.impl;

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.Item;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.NotAuthenticatedException;
import org.dd4t.contentmodel.exceptions.NotAuthorizedException;
import org.dd4t.core.factories.impl.GenericComponentFactory;
import org.dd4t.core.filters.Filter;
import org.dd4t.core.filters.FilterException;
import org.dd4t.core.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pagefactory filter intended to resolve DCP's on pages at the factory level. It checks the page
 * being produced, finds the dynamic components (if any), and resolves these components through
 * the ComponentFactory.
 * 
 * @author Rogier Oudshoorn
 *
 */
public class HybridPublishingFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(HybridPublishingFilter.class);
	
	private GenericComponentFactory genericComponentFactory;
	
	@Override
	public void doFilter(Item item, RequestContext rc)
			throws FilterException, NotAuthorizedException,
			NotAuthenticatedException {
		
		if(logger.isDebugEnabled()){
			logger.debug("[HybridPublishingFilter] acting upon item "+item);
		}
		
		// filter only acts on pages
		if(item instanceof GenericPage){
			GenericPage page = (GenericPage) item;
			
			if(logger.isDebugEnabled()){
				logger.debug("[HybridPublishingFilter] Detected "+page.getComponentPresentations().size() +" component presentations.");
			}
			
			for(ComponentPresentation cp : page.getComponentPresentations()){
				if(cp.isDynamic()){
					if(logger.isDebugEnabled()){
						logger.debug("[HybridPublishingFilter] Detected dynamic component presentation "+cp);
					}
					
					long start = System.currentTimeMillis();

					try{
						// retrieve the dynamic component based on template
						GenericComponent comp = (GenericComponent) genericComponentFactory.getComponent(
								cp.getComponent().getId(), 
								cp.getComponentTemplate().getId(),
								rc);
												
						// set the dynamic component
						cp.setComponent(comp);												
					}					
					// note: the other exceptions (authorization & authentication) are passed on
					catch(ItemNotFoundException ex){
						logger.error("Unable to find component by id "+cp.getComponent().getId(), ex);
					}
					
					long done = System.currentTimeMillis();
					
					if(logger.isInfoEnabled()){
						logger.info("[HybridPublishingFilter] Processed Dynamic Assembly in "+(done-start)+" milliseconds.");
					}
				}
			}			
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("[HybridPublishingFilter] exits for item "+item);
		} 

	}

	@Override
	public boolean getCachingAllowed() {
		return false;
	}

	@Override
	public void setCachingAllowed(boolean arg0) {

	}

	public GenericComponentFactory getGenericComponentFactory() {
		return genericComponentFactory;
	}

	public void setGenericComponentFactory(GenericComponentFactory genericComponentFactory) {
		this.genericComponentFactory = genericComponentFactory;
	}

}

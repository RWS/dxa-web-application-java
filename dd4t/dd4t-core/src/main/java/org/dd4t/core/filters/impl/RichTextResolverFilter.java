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
package org.dd4t.core.filters.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.Item;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.XhtmlField;
import org.dd4t.core.filters.Filter;
import org.dd4t.core.filters.FilterException;
import org.dd4t.core.request.RequestContext;
import org.dd4t.core.util.XSLTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;


/**
 * Filter to resolve component links.
 * 
 * @author bjornl
 * 
 */
public class RichTextResolverFilter extends BaseFilter implements
		Filter {

	
	private static Logger logger = LoggerFactory.getLogger(RichTextResolverFilter.class);

	private XSLTransformer xslTransformer = new XSLTransformer();
	
	public RichTextResolverFilter() {
		this.setCachingAllowed(true);
	}

	/**
	 * Recursivly resolves all components links.
	 * 
	 * @param item
	 *            the to resolve the links
	 * @param context
	 *            the requestContext
	 */
	@Override
	public void doFilter(Item item, RequestContext context)
			throws FilterException {

		StopWatch stopWatch = null;
		if (logger.isDebugEnabled()) {
			stopWatch = new StopWatch();
			stopWatch.start();
		}
		if (item instanceof GenericPage) {
			resolvePage((GenericPage) item);
		} else if (item instanceof GenericComponent) {
			resolveComponent((GenericComponent) item);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("RichTextResolverFilter. Item is not a GenericPage or GenericComponent so no component to resolve");
			}
		}
		if (logger.isDebugEnabled()) {
			stopWatch.stop();
			logger.debug("RichTextResolverFilter finished in "
					+ stopWatch.getTotalTimeMillis() + " ms");
		}
	}

	protected void resolvePage(GenericPage page) {

		List<ComponentPresentation> cpList = page.getComponentPresentations();
		if (cpList != null) {
			for (ComponentPresentation cp : cpList) {
				resolveComponent((GenericComponent) cp.getComponent());
			}
		}
		resolveMap(page.getMetadata());
	}

	protected void resolveComponent(GenericComponent component) {

		if (component != null) {
			resolveMap(component.getContent());
			resolveMap(component.getMetadata());
		}
	}

	protected void resolveMap(Map<String, Field> fieldMap) {

		if (fieldMap != null && !fieldMap.isEmpty()) {
			Collection<Field> values = fieldMap.values();
			for (Field field : values) {
				if (field instanceof XhtmlField) {
					resolveXhtmlField((XhtmlField) field);
				}
				if (field instanceof EmbeddedField) {
                                    EmbeddedField ef = (EmbeddedField) field;
                                    
                                    for(FieldSet fs : ef.getEmbeddedValues()){
                                        resolveMap(fs.getContent());
                                    }
				}
			}
		}
	}

	protected void resolveXhtmlField(XhtmlField xhtmlField) {

		List<Object> xhtmlValues = xhtmlField.getValues();
		List<String> newValues = new ArrayList<String>();

		// find all component links and try to resolve them
		Pattern p = Pattern.compile("</?ddtmproot>");
		for (Object xhtmlValue : xhtmlValues) {
			String result = xslTransformer.transformSourceFromFilesource("<ddtmproot>" + (String)xhtmlValue + "</ddtmproot>", "/resolveXhtml.xslt");
			newValues.add(p.matcher(result).replaceAll(""));
		}
		xhtmlField.setTextValues(newValues);
	}


}
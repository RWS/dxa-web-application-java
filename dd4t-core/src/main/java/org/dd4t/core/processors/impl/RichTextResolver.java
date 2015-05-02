/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.core.processors.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Item;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.XhtmlField;
import org.dd4t.core.exceptions.ProcessorException;
import org.dd4t.core.processors.Processor;
import org.dd4t.core.request.RequestContext;
import org.dd4t.core.util.XSLTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter to resolve component links.
 *
 * @author bjornl, rogier oudshoorn
 */
public class RichTextResolver extends BaseProcessor implements Processor {

	private static final Logger LOG = LoggerFactory.getLogger(RichTextResolver.class);
	private final XSLTransformer xslTransformer = XSLTransformer.getInstance();

	public RichTextResolver () {
	}

	/**
	 * Recursively resolves all components links.
	 *
	 * @param item    the to resolve the links
	 */
	@Override
	public void execute (Item item, RequestContext context) throws ProcessorException {
		if (item instanceof Page) {
			try {
				resolvePage((Page)item);
			} catch (TransformerException e) {
				LOG.error(e.getMessage(), e);
				throw new ProcessorException(e);
			}
		} else if (item instanceof ComponentPresentation) {
			try {
				resolveComponent(((ComponentPresentation) item).getComponent());
			} catch (TransformerException e) {
				LOG.error(e.getMessage(), e);
				throw new ProcessorException(e);
			}
		} else if (item instanceof Component) {
			try {
				resolveComponent((Component) item);
			} catch (TransformerException e) {
				LOG.error(e.getMessage(), e);
				throw new ProcessorException(e);
			}
		} else {
			LOG.debug("RichTextResolverFilter. Item is not a GenericPage or GenericComponent so no component to resolve");
		}
		LOG.debug("RichTextResolverFilter finished");

	}

	protected void resolvePage (Page page) throws TransformerException {
		List<ComponentPresentation> cpList = page.getComponentPresentations();
		if (cpList != null) {
			for (ComponentPresentation cp : cpList) {
				resolveComponent(cp.getComponent());
			}
		}
		resolveMap(page.getMetadata());
	}

	protected void resolveComponent (Component component) throws TransformerException {
		if (component != null) {
			resolveMap(component.getContent());
			resolveMap(component.getMetadata());
		}
	}

	protected void resolveMap (Map<String, Field> fieldMap) throws TransformerException {
		if (fieldMap == null || fieldMap.isEmpty()) {
			return;
		}
		Collection<Field> values = fieldMap.values();
		for (Field field : values) {
			if (field instanceof XhtmlField) {
				resolveXhtmlField((XhtmlField) field);
			}
			if (field instanceof EmbeddedField) {
				EmbeddedField ef = (EmbeddedField) field;

				for (FieldSet fs : ef.getEmbeddedValues()) {
					resolveMap(fs.getContent());
				}
			}
		}
	}

	protected void resolveXhtmlField (XhtmlField xhtmlField) throws TransformerException {
		List<Object> xhtmlValues = xhtmlField.getValues();
		List<String> newValues = new ArrayList<String>();

		Pattern p = Pattern.compile("</?ddtmproot>");
		for (Object xhtmlValue : xhtmlValues) {
			String result = xslTransformer.transformSourceFromFilesource("<ddtmproot>" + xhtmlValue + "</ddtmproot>", "/resolveXhtml.xslt");
			newValues.add(p.matcher(result).replaceAll(""));
		}
		xhtmlField.setTextValues(newValues);
	}
}
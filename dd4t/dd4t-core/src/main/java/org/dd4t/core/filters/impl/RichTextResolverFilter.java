package org.dd4t.core.filters.impl;

import org.dd4t.contentmodel.*;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.XhtmlField;
import org.dd4t.core.filters.Filter;
import org.dd4t.core.filters.FilterException;
import org.dd4t.core.util.XSLTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Filter to resolve component links.
 *
 * @author bjornl
 */
public class RichTextResolverFilter extends BaseFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(RichTextResolverFilter.class);
	private final XSLTransformer xslTransformer = XSLTransformer.getInstance();

	public RichTextResolverFilter () {
		setCachingAllowed(true);
	}

	/**
	 * Recursivly resolves all components links.
	 *
	 * @param item    the to resolve the links
	 */
	@Override
	public void doFilter (Item item) throws FilterException {
		if (item instanceof GenericPage) {
			try {
				resolvePage((GenericPage) item);
			} catch (TransformerException e) {
				LOG.error(e.getMessage(), e);
				throw new FilterException(e);
			}
		} else if (item instanceof GenericComponent) {
			try {
				resolveComponent((GenericComponent) item);
			} catch (TransformerException e) {
				LOG.error(e.getMessage(), e);
				throw new FilterException(e);
			}
		} else {
			LOG.debug("RichTextResolverFilter. Item is not a GenericPage or GenericComponent so no component to resolve");
		}
		LOG.debug("RichTextResolverFilter finished");

	}

	protected void resolvePage (GenericPage page) throws TransformerException {
		List<ComponentPresentation> cpList = page.getComponentPresentations();
		if (cpList != null) {
			for (ComponentPresentation cp : cpList) {
				resolveComponent(cp.getComponent());
			}
		}
		resolveMap(page.getMetadata());
	}

	protected void resolveComponent (GenericComponent component) throws TransformerException {
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

		// find all component links and try to resolve them
		Pattern p = Pattern.compile("</?ddtmproot>");
		for (Object xhtmlValue : xhtmlValues) {
			String result = xslTransformer.transformSourceFromFilesource("<ddtmproot>" + xhtmlValue + "</ddtmproot>", "/resolveXhtml.xslt");
			newValues.add(p.matcher(result).replaceAll(""));
		}
		xhtmlField.setTextValues(newValues);
	}
}
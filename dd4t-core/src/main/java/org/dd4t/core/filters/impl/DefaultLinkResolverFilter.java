package org.dd4t.core.filters.impl;

import org.dd4t.contentmodel.*;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.contentmodel.impl.ComponentLinkField;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.XhtmlField;
import org.dd4t.core.factories.impl.LinkResolverFactory;
import org.dd4t.core.exceptions.FilterException;
import org.dd4t.core.filters.LinkResolverFilter;
import org.dd4t.core.resolvers.LinkResolver;
import org.dd4t.core.util.TridionUtils;
import org.dd4t.core.util.XSLTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter to resolve component links.
 *
 * @author bjornl
 */
public class DefaultLinkResolverFilter extends BaseFilter implements LinkResolverFilter {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultLinkResolverFilter.class);
	// TODO: remove
    private static final boolean USE_XSLT = true;
    private final XSLTransformer xslTransformer = XSLTransformer.getInstance();
    private final Pattern xsltPattern = Pattern.compile("</?ddtmproot>");
    private final Pattern regExpPattern = Pattern.compile("href=\"" + TridionUtils.TCM_REGEX + "\"");
    private LinkResolver linkResolver;
    private String contextPath;
    private Map<String, Object> params = new HashMap<>();

    public DefaultLinkResolverFilter() {
        setCachingAllowed(false);
        LinkResolverFactory factory = LinkResolverFactory.getInstance();
        setLinkResolver(factory.getLinkResolver());
    }

    /**
     * Recursively resolves all components links.
     *
     * @param item    the to resolve the links
     */
    @Override
    public void doFilter(Item item) throws FilterException {
        linkResolver.setContextPath(contextPath);

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
            LOG.debug("DefaultLinkResolverFilter. Item is not a GenericPage or GenericComponent so no component to resolve");
        }
    }

    protected void resolvePage(GenericPage page) throws TransformerException {
        List<ComponentPresentation> cpList = page.getComponentPresentations();
        if (cpList != null) {
            for (ComponentPresentation cp : cpList) {
                resolveComponent(cp.getComponent(), page);
            }
        }
        resolveMap(page.getMetadata());
    }

    protected void resolveComponent(Component component, GenericPage page) throws TransformerException {
        if (component != null) {
            // resolve regular content
            resolveMap(component.getContent());
            // resolve metadata
            resolveMap(component.getMetadata());
        }
    }

    protected void resolveComponent(GenericComponent component) throws TransformerException {
        try {
            if (component != null) {
                resolveMap(component.getContent());
                resolveMap(component.getMetadata());
                linkResolver.resolve(component);
            }
        } catch (ItemNotFoundException | SerializationException e) {
            throw new TransformerException(e);
        }
    }

    protected void resolveMap(Map<String, Field> fieldMap) throws TransformerException {
        if (fieldMap != null && !fieldMap.isEmpty()) {
            Collection<Field> values = fieldMap.values();
            for (Field field : values) {
                if (field instanceof ComponentLinkField) {
                    resolveComponentLinkField((ComponentLinkField) field);
                } else if (field instanceof EmbeddedField) {
                    resolveList(((EmbeddedField) field).getEmbeddedValues());
                } else if (field instanceof XhtmlField) {
                    resolveXhtmlField((XhtmlField) field);
                }
            }
        }
    }

    protected void resolveList(List<FieldSet> fslist) throws TransformerException {
        if (fslist != null && !fslist.isEmpty()) {
            for (FieldSet fs : fslist) {
                resolveMap(fs.getContent());
            }
        }
    }

    protected void resolveComponentLinkField(ComponentLinkField componentLinkField) throws TransformerException {
        List<Object> compList = componentLinkField.getValues();

        for (Object component : compList) {
            resolveComponent((GenericComponent) component);
        }
    }

    protected void resolveXhtmlField(XhtmlField xhtmlField) throws TransformerException {
        List<Object> xhtmlValues = xhtmlField.getValues();
        List<String> newValues = new ArrayList<>();

        if (USE_XSLT) {
            // find all component links and try to resolve them
            for (Object xhtmlValue : xhtmlValues) {
                String result = xslTransformer.transformSourceFromFilesource("<ddtmproot>" + xhtmlValue + "</ddtmproot>", "/resolveXhtmlWithLinks.xslt", params);
                newValues.add(xsltPattern.matcher(result).replaceAll(""));
            }
        } else {
            try {
                // find all component links and try to resolve them
                for (Object xhtmlValue : xhtmlValues) {

                    Matcher m = regExpPattern.matcher((String) xhtmlValue);

	                // TODO: StringBuffers are expensive! Replace by StringBuilder mechanism

                    StringBuffer sb = new StringBuffer();
                    String resolvedLink = null;
                    while (m.find()) {
                        resolvedLink = linkResolver.resolve(m.group(1));
                        // if not possible to resolve the link do nothing
                        if (resolvedLink != null) {
                            m.appendReplacement(sb, "href=\"" + resolvedLink + "\"");
                        }
                    }
                    m.appendTail(sb);
                    newValues.add(sb.toString());
                }
            } catch (ItemNotFoundException | SerializationException e) {
                throw new TransformerException(e);
            }
        }

        xhtmlField.setTextValues(newValues);
    }

    public LinkResolver getLinkResolver() {
        return linkResolver;
    }

    public void setLinkResolver(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }

    @Override
    public String getContextPath() {
        return this.contextPath;
    }

    @Override
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
        this.params = new HashMap<>();
        this.params.put("contextpath", contextPath);
    }
}
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.Item;
import org.dd4t.contentmodel.impl.ComponentLinkField;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.XhtmlField;
import org.dd4t.core.filters.FilterException;
import org.dd4t.core.filters.LinkResolverFilter;
import org.dd4t.core.request.RequestContext;
import org.dd4t.core.resolvers.LinkResolver;
import org.dd4t.core.resolvers.impl.DefaultLinkResolver;
import org.dd4t.core.util.TridionUtils;
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
public class DefaultLinkResolverFilter extends BaseFilter implements
        LinkResolverFilter {

    private boolean useXslt = true;

    private String contextPath;

    private LinkResolver linkResolver;

    private static Logger logger = LoggerFactory.getLogger(DefaultLinkResolverFilter.class);

    private XSLTransformer xslTransformer = new XSLTransformer();

    private Map<String, Object> params = new HashMap<String, Object>();

    private Pattern XSLTPattern;

    private Pattern RegExpPattern;

    public DefaultLinkResolverFilter() {

        XSLTPattern = Pattern.compile("</?ddtmproot>");
        RegExpPattern =
                Pattern.compile("href=\"" + TridionUtils.TCM_REGEX + "\"");

        this.setCachingAllowed(false);
    }

    /**
     * Recursively resolves all components links.
     * 
     * @param item the to resolve the links
     * @param context the requestContext
     */
    @Override
    public void doFilter(Item item, RequestContext context)
            throws FilterException {

        this.getLinkResolver().setContextPath(contextPath);

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
                logger.debug("DefaultLinkResolverFilter. Item is not a GenericPage or GenericComponent so no component to resolve");
            }
        }
        if (logger.isDebugEnabled()) {
            stopWatch.stop();
            logger.debug("LinkResolverFilter for " + item.getId()
                    + " finished in " + stopWatch.getTotalTimeMillis() + " ms");
        }
    }

    protected void resolvePage(GenericPage page) {

        StopWatch stopWatch = null;
        if (logger.isDebugEnabled()) {
            stopWatch = new StopWatch();
            stopWatch.start();
        }

        List<ComponentPresentation> cpList = page.getComponentPresentations();
        if (cpList != null) {
            for (ComponentPresentation cp : cpList) {
                resolveComponent((GenericComponent) cp.getComponent(), page);
            }
        }
        resolveMap(page.getMetadata());

        if (logger.isDebugEnabled()) {
            stopWatch.stop();
            logger.debug("ResolvePage for " + page.getId() + " finished in "
                    + stopWatch.getTotalTimeMillis() + " ms");
        }
    }

    protected void resolveComponent(GenericComponent component, GenericPage page) {

        StopWatch stopWatch = null;
        if (logger.isDebugEnabled()) {
            stopWatch = new StopWatch();
            stopWatch.start();
        }

        if (component != null) {
            // resolve regular content
            resolveMap(component.getContent());
            // resolve metadata
            resolveMap(component.getMetadata());
            /* don't resolve Component itself; 
             * this may very likely lead to performance issues when using
             * the experience manager.
             */
            // getLinkResolver().resolve(component, page);

        }

        if (logger.isDebugEnabled()) {
            stopWatch.stop();
            logger.debug("ResolveComponent for " + component.getId()
                    + " finished in " + stopWatch.getTotalTimeMillis() + " ms");
        }
    }

    protected void resolveComponent(GenericComponent component) {

        if (component != null) {
            resolveMap(component.getContent());
            resolveMap(component.getMetadata());
            getLinkResolver().resolve(component);
        }
    }

    protected void resolveMap(Map<String, Field> fieldMap) {

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

    protected void resolveList(List<FieldSet> fslist) {

        if (fslist != null && !fslist.isEmpty()) {
            for (FieldSet fs : fslist) {
                resolveMap(fs.getContent());
            }
        }
    }

    protected void resolveComponentLinkField(
            ComponentLinkField componentLinkField) {

        StopWatch stopWatch = null;
        if (logger.isDebugEnabled()) {
            stopWatch = new StopWatch();
            stopWatch.start();
        }
        List<Object> compList = componentLinkField.getValues();

        for (Object component : compList) {
            resolveComponent((GenericComponent) component);
        }

        if (logger.isDebugEnabled()) {
            stopWatch.stop();
            logger.debug("Resolved componentLinkField '"
                    + componentLinkField.getName() + "' in "
                    + stopWatch.getTotalTimeMillis() + " ms.");
        }

    }

    protected void resolveXhtmlField(XhtmlField xhtmlField) {

        StopWatch stopWatch = null;
        if (logger.isDebugEnabled()) {
            stopWatch = new StopWatch();
            stopWatch.start();
        }

        List<Object> xhtmlValues = xhtmlField.getValues();
        List<String> newValues = new ArrayList<String>();

        if (useXslt) {
            // find all component links and try to resolve them
            for (Object xhtmlValue : xhtmlValues) {
                String result =
                        xslTransformer.transformSourceFromFilesource(
                                "<ddtmproot>" + (String) xhtmlValue
                                        + "</ddtmproot>",
                                "/resolveXhtmlWithLinks.xslt", params);
                newValues.add(XSLTPattern.matcher(result).replaceAll(""));
            }
        } else {
            // find all component links and try to resolve them
            for (Object xhtmlValue : xhtmlValues) {

                Matcher m = RegExpPattern.matcher((String) xhtmlValue);

                StringBuffer sb = new StringBuffer();
                String resolvedLink = null;
                while (m.find()) {
                    resolvedLink = getLinkResolver().resolve(m.group(1));
                    // if not possible to resolve the link do nothing
                    if (resolvedLink != null) {
                        m.appendReplacement(sb, "href=\"" + resolvedLink + "\"");
                    }
                }
                m.appendTail(sb);
                newValues.add(sb.toString());
            }

        }

        xhtmlField.setTextValues(newValues);

        if (logger.isDebugEnabled()) {
            stopWatch.stop();
            logger.debug("Parsed rich text field '" + xhtmlField.getName()
                    + "' in " + stopWatch.getTotalTimeMillis() + " ms.");
        }
    }

    public LinkResolver getLinkResolver() {

        if (linkResolver == null) {
            linkResolver = new DefaultLinkResolver();
        }
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
        this.params = new HashMap<String, Object>();
        this.params.put("contextpath", contextPath);
    }

}
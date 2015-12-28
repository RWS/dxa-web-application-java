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

package org.dd4t.core.resolvers.impl;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.Schema;
import org.dd4t.contentmodel.impl.PublicationImpl;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.resolvers.LinkResolver;
import org.dd4t.core.util.TCMURI;
import org.dd4t.core.util.TridionUtils;
import org.dd4t.providers.LinkProvider;
import org.dd4t.providers.PayloadCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class DefaultLinkResolver implements LinkResolver {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultLinkResolver.class);

    @Resource
    private LinkProvider linkProvider;
    @Resource
    private PayloadCacheProvider cacheProvider;
    private Map<String, String> schemaToUrlMappings;
    private String schemaKey;
    private boolean encodeUrl = true;
    private String contextPath;

    private DefaultLinkResolver () {
        LOG.debug("Create new instance");
    }

    @Override
    public String resolve (ComponentPresentation cp) throws ItemNotFoundException, SerializationException {
        return resolve(cp.getComponent(), null);
    }

    @Override
    public String resolve (Component component) throws ItemNotFoundException, SerializationException {
        return resolve(component, null);
    }

    @Override
    public String resolve (Component component, Page page) throws ItemNotFoundException, SerializationException {
        LOG.debug("Resolving link to component: {} from page: {}", component, page);
        String resolvedUrl = null;
        if (component == null) {
            return null;
        }
        // option 1 - handle multimedia

        if (component.getMultimedia() != null) {
            resolvedUrl = component.getMultimedia().getUrl();
        }

        Schema schema = component.getSchema();

        // option 2 - handle by schema
        if (resolvedUrl == null) {
            resolvedUrl = findUrlMapping(schema);
        }

        // option 3 - use componentLinker
        if (StringUtils.isEmpty(resolvedUrl)) {
            verifyPublicationIsSet(component);

            if (page == null) {
                resolvedUrl = resolve(component.getId());
            } else {
                resolvedUrl = resolve(component.getId(), page.getId());
            }

            if (StringUtils.isEmpty(resolvedUrl)) {
                LOG.warn("Not possible to resolve url for component: " + component.getId());
            }
        } else {
            resolvedUrl = replacePlaceholders(resolvedUrl, "%COMPONENTURI%", component.getId());
            resolvedUrl = replacePlaceholders(resolvedUrl, "%COMPONENTTITLE%", component.getTitle());
            resolvedUrl = replacePlaceholders(resolvedUrl, "%SCHEMAURI%", schema.getId());
            resolvedUrl = replacePlaceholders(resolvedUrl, "%SCHEMATITLE%", schema.getTitle());
        }
        if (contextPath != null && contextPath.length() > 0) {
            resolvedUrl = contextPath + resolvedUrl;
        }
        return resolvedUrl;
    }

    private static void verifyPublicationIsSet (final Component component) {
        if (component.getPublication() == null) {
            try {
                TCMURI tcmUri = new TCMURI(component.getId());
                component.setPublication(new PublicationImpl(TridionUtils.constructFullTcmPublicationUri(tcmUri.getPublicationId())));
            } catch (ParseException e) {
                LOG.error("Problem parsing the uri for component: " + component.getId(), e);
            }
        }
    }

    @Override
    public String resolve (String componentURI) throws ItemNotFoundException, SerializationException {
        return resolve(componentURI, null);
    }

    private boolean validInCache (CacheElement<String> cacheElement) {
        if (cacheElement.isExpired()) {
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String resolve (String componentURI, String pageURI) throws ItemNotFoundException, SerializationException {
        String key;
        if (!StringUtils.isEmpty(pageURI)) {
            key = getCacheKey(componentURI, pageURI);
        } else {
            key = getCacheKey(componentURI);
        }
        CacheElement<String> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);
        String result;

        if (!validInCache(cacheElement)) {
            if (!StringUtils.isEmpty(pageURI)) {
                result = linkProvider.resolveComponentFromPage(componentURI, pageURI);
            } else {
                result = linkProvider.resolveComponent(componentURI);
            }

            try {
                result = addToCache(componentURI, key, cacheElement, result);
            } catch (ParseException e) {
                String message = String.format("Invalid ComponentURI %s", componentURI);
                LOG.error(message);
                throw new SerializationException(message, e);
            }
        } else {
            result = cacheElement.getPayload();
            LOG.debug("Return link url: {} for uri: {} from cache", result, componentURI);
        }

        return result;
    }


    private String addToCache (String componentURI, String key, CacheElement<String> cacheElement, String result) throws ParseException {
        result = result == null ? "" : result;
        cacheElement.setPayload(result);

        TCMURI tcmUri = new TCMURI(componentURI);
        cacheProvider.storeInItemCache(key, cacheElement, tcmUri.getPublicationId(), tcmUri.getItemId());
        LOG.debug("Added link url: {} for uri: {} to cache", result, componentURI);
        return result;
    }

    private String getCacheKey (String componentURI) {
        return String.format("CL-%s", componentURI);
    }

    private String getCacheKey (String componentURI, String pageURI) {
        return String.format("CL-%s-%s", componentURI, pageURI);
    }

    private String replacePlaceholders (String resolvedUrl, String placeholder, String replacementText) {
        StringBuffer sb = new StringBuffer();
        if (!StringUtils.isEmpty(replacementText)) {
            if (getEncodeUrl()) {
                try {
                    replacementText = URLEncoder.encode(replacementText, CharEncoding.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    LOG.warn("Not possible to encode string: " + replacementText, e);
                    return "";
                }
            }

            Pattern p = Pattern.compile(placeholder);
            Matcher m = p.matcher(resolvedUrl);

            while (m.find()) {
                m.appendReplacement(sb, replacementText);
            }
            m.appendTail(sb);
        }
        return sb.toString();
    }

    private String findUrlMapping (Schema schema) {
        String key = "";
        if ("id".equals(schemaKey)) {
            try {
                TCMURI tcmUri = new TCMURI(schema.getId());
                key = String.valueOf(tcmUri.getItemId());
            } catch (ParseException e) {
                LOG.error(e.getMessage(), e);
                return null;
            }
        } else if ("title".equals(schemaKey)) {
            key = schema.getTitle();
        } else {
            // use uri as default key
            key = schema.getId();
        }

        return getSchemaToUrlMappings().get(key);
    }

    public Map<String, String> getSchemaToUrlMappings () {
        if (schemaToUrlMappings == null) {
            this.schemaToUrlMappings = new HashMap<String, String>();
        }
        return schemaToUrlMappings;
    }

    // TODO: where is this set?
    public void setSchemaToUrlMappings (Map<String, String> schemaToUrlMappings) {
        this.schemaToUrlMappings = schemaToUrlMappings;
    }

    public String getSchemaKey () {
        return schemaKey;
    }

    public void setSchemaKey (String schemaKey) {
        this.schemaKey = schemaKey;
    }

    public boolean getEncodeUrl () {
        return encodeUrl;
    }

    public void setEncodeUrl (boolean encodeUrl) {
        this.encodeUrl = encodeUrl;
    }

    @Override
    public String getContextPath () {
        if (contextPath == null) {
            contextPath = "";
        }
        return contextPath;
    }

    @Override
    public void setContextPath (String contextPath) {
        this.contextPath = contextPath;
    }

    public LinkProvider getLinkProvider () {
        return linkProvider;
    }

    public void setLinkProvider (LinkProvider linkProvider) {
        this.linkProvider = linkProvider;
    }

    public PayloadCacheProvider getCacheProvider () {
        return cacheProvider;
    }

    public void setCacheProvider (PayloadCacheProvider cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

}

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
package org.dd4t.core.resolvers.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.Schema;
import org.dd4t.contentmodel.impl.PublicationImpl;
import org.dd4t.core.resolvers.LinkResolver;
import org.dd4t.core.util.TridionUtils;
import org.dd4t.providers.LinkProvider;
import org.dd4t.providers.impl.BrokerLinkProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tridion.util.TCMURI;

public class DefaultLinkResolver implements LinkResolver {

	private Map<String, String> schemaToUrlMappings;
	private String schemaKey;
	private boolean encodeUrl = true;
	private String contextPath;
	
	private LinkProvider linkProvider;

	private static Logger logger = LoggerFactory.getLogger(DefaultLinkResolver.class);

	@Override
	public String resolve(Component component) {
		return resolve(component,null);
	}
	
	@Override
	public String resolve(Component component, Page page) {

		String resolvedUrl = null;
		if (component != null) {
		    // option 1 - handle multimedia
		    if(component instanceof GenericComponent){
		        GenericComponent comp = (GenericComponent) component;
		        if(comp.getMultimedia() != null){
		            resolvedUrl = comp.getMultimedia().getUrl();
		        }		        
		    }	    			
			
		    Schema schema = component.getSchema();
		    
			// option 2 - handle by schema
			if(resolvedUrl == null){			    
			    resolvedUrl = findUrlMapping(schema);
			}

			// option 3 - use componentLinker
			if (resolvedUrl == null) {
				if (component.getPublication() == null) {
					try {
						TCMURI tcmUri = new TCMURI(component.getId());
						component.setPublication(new PublicationImpl(
								TridionUtils.getPublicationUri(tcmUri
										.getPublicationId())));
					} catch (ParseException e) {
						if (logger.isDebugEnabled()) {
							logger.debug(
									"Problem parsing the uri for component: "
											+ component.getId(), e);
						}
					}
				}
				if (component.getPublication() != null) {
					if (page == null) {
						resolvedUrl = resolve(component.getId());	
					} else {
						resolvedUrl = resolve(component.getId(), page.getId());	
					}
				}
				if (logger.isDebugEnabled()
						&& (resolvedUrl == null || "".equals(resolvedUrl))) {
					logger.debug("Not possible to resolve url for component: "
							+ component.getId());
				}

			} else {
				resolvedUrl = replacePlaceholders(resolvedUrl,
						"%COMPONENTURI%", component.getId());
				resolvedUrl = replacePlaceholders(resolvedUrl,
						"%COMPONENTTITLE%", component.getTitle());
				resolvedUrl = replacePlaceholders(resolvedUrl, "%SCHEMAURI%",
						schema.getId());
				resolvedUrl = replacePlaceholders(resolvedUrl, "%SCHEMATITLE%",
						schema.getTitle());
			}
			if (contextPath != null && contextPath.length() > 0) {
				resolvedUrl = contextPath + resolvedUrl;			
			} 

			component.setResolvedUrl(resolvedUrl);			
		}
		
		return resolvedUrl;
	}	

	@Override
	public String resolve(String componentId) {
		return getLinkProvider().resolveComponent(componentId);
	}

	@Override
	public String resolve(String componentId, String pageId) {
		return getLinkProvider().resolveComponentFromPage(componentId, pageId);			
	}

	private String replacePlaceholders(String resolvedUrl, String placeholder,
			String replacementText) {

		StringBuffer sb = new StringBuffer();
		if (replacementText != null && !"".equals(replacementText)) {
			if (getEncodeUrl()) {
				try {
					replacementText = URLEncoder.encode(replacementText,
							"UTF-8");
				} catch (UnsupportedEncodingException e) {
					logger.warn("Not possible to encode string: "
							+ replacementText, e);
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

	private String findUrlMapping(Schema schema) {

		String key = "";
		if ("id".equals(schemaKey)) {
			try {
				TCMURI tcmUri = new TCMURI(schema.getId());
				key = String.valueOf(tcmUri.getItemId());
			} catch (ParseException e) {
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

	public Map<String, String> getSchemaToUrlMappings() {
		if (schemaToUrlMappings == null) {
			this.schemaToUrlMappings = new HashMap<String, String>();
		}
		return schemaToUrlMappings;
	}

	public void setSchemaToUrlMappings(Map<String, String> schemaToUrlMappings) {
		this.schemaToUrlMappings = schemaToUrlMappings;
	}

	public String getSchemaKey() {
		return schemaKey;
	}

	public void setSchemaKey(String schemaKey) {
		this.schemaKey = schemaKey;
	}

	public boolean getEncodeUrl() {
		return encodeUrl;
	}

	public void setEncodeUrl(boolean encodeUrl) {
		this.encodeUrl = encodeUrl;
	}
	
	@Override
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	@Override
	public String getContextPath() {
		return contextPath;
	}

	public LinkProvider getLinkProvider() {
		if(linkProvider == null){
			linkProvider = new BrokerLinkProvider();
		}
		return linkProvider;
	}

	public void setLinkProvider(LinkProvider linkProvider) {
		this.linkProvider = linkProvider;
	}

}

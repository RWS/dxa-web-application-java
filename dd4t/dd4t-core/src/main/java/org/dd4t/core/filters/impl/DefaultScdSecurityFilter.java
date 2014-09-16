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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.Item;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.PublishedItem;
import org.dd4t.contentmodel.SimpleComponent;
import org.dd4t.contentmodel.SimplePage;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.NotAuthenticatedException;
import org.dd4t.contentmodel.exceptions.NotAuthorizedException;
import org.dd4t.contentmodel.impl.ComponentLinkField;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.XhtmlField;
import org.dd4t.core.factories.impl.SimpleComponentFactory;
import org.dd4t.core.filters.FilterException;
import org.dd4t.core.filters.SecurityFilter;
import org.dd4t.core.request.RequestContext;
import org.dd4t.core.util.TridionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tridion.meta.Category;
import com.tridion.meta.ComponentMeta;
import com.tridion.meta.ComponentMetaFactory;
import com.tridion.meta.PageMeta;
import com.tridion.meta.PageMetaFactory;
import com.tridion.taxonomies.Keyword;

public class DefaultScdSecurityFilter extends BaseFilter implements
		SecurityFilter {

	private int depth = 0;
	private ActionIfDenied actionIfDenied = ActionIfDenied.Block;
	public final static String ACCESS_DENIED_PROPERTY_NAME = "accessDenied";
	private String roleCategory;
	private SimpleComponentFactory simpleComponentFactory = new SimpleComponentFactory();

	private static Logger logger = LoggerFactory.getLogger(DefaultScdSecurityFilter.class);

	public DefaultScdSecurityFilter() {
		// set the caching to false to make sure that the security filter is run
		// every request to check if the user is allowed to view the item
		setCachingAllowed(false);
	}

	@Override
	public void doFilter(Item item, RequestContext context)
			throws FilterException, NotAuthorizedException, NotAuthenticatedException {

		if (!(item instanceof PublishedItem)) {
			throw new FilterException(
					"SecurityFilter can only be applied to PublishedItems");
		}

		if (item instanceof SimplePage) {
			checkAuthorizationSimplePage((SimplePage) item, context);
		} else if (item instanceof SimpleComponent) {
			checkAuthorizationSimpleComponent((SimpleComponent) item, context);
		} else if (item instanceof GenericPage) {
			checkAuthorizationGenericPage((GenericPage) item, context, depth);
		} else if (item instanceof GenericComponent) {
			try {
				checkAuthorizationGenericComponent((GenericComponent) item,
						context, true, depth);
			} catch (FilterOutException e) {
				// this should never happen since it is a root item
				logger.warn("FilterOutException thrown for item: "
						+ item.getId());
			}
		}
	}

	private void checkAuthorizationSimplePage(SimplePage page,
			RequestContext context) throws NotAuthorizedException, NotAuthenticatedException {

		try {
			checkAccessNativeMetadata(page, context, true);
		} catch (FilterOutException e) {
			// this should never happen since it is a root item
			logger.warn("FilterOutException thrown for item: " + page.getId());
		}
	}

	private void checkAuthorizationSimpleComponent(SimpleComponent component,
			RequestContext context) throws NotAuthorizedException, NotAuthenticatedException {

		try {
			checkAccessNativeMetadata(component, context, true);
		} catch (FilterOutException e) {
			// this should never happen since it is a root item
			logger.warn("FilterOutException thrown for item: "
					+ component.getId());
			
		}
	}

	private void checkAuthorizationGenericPage(GenericPage page,
			RequestContext context, int depth) throws NotAuthorizedException, NotAuthenticatedException {
		// check current page first
		try {
			checkAccessNativeMetadata(page, context, true);
		} catch (FilterOutException e) {
			// this should never happen since it is a root item
			logger.warn("FilterOutException thrown for item: " + page.getId());
		}

		if (depth > 0) {
			int nextDepth = depth - 1;
			// check all metadata component links
			checkAccessFieldsMap(page.getMetadata(), context, nextDepth);

			// check all components on the page
			List<ComponentPresentation> cpList = page
					.getComponentPresentations();
			List<ComponentPresentation> deleteList = new ArrayList<ComponentPresentation>();
			for (ComponentPresentation cp : cpList) {
				try {
					checkAuthorizationGenericComponent(
							(GenericComponent) cp.getComponent(), context,
							false, nextDepth);
				} catch (FilterOutException e) {
					deleteList.add(cp);
				}
			}
			if (!deleteList.isEmpty()) {
				for (ComponentPresentation cp : deleteList) {
					page.getComponentPresentations().remove(cp);
				}
			}
		}
	}

	private void checkAuthorizationGenericComponent(GenericComponent component,
			RequestContext context, boolean isRoot, int depth)
			throws NotAuthorizedException, FilterOutException, NotAuthenticatedException {

		checkAccessNativeMetadata(component, context, isRoot);

		if (depth > 0) {
			int nextDepth = depth - 1;
			// check the metadata
			checkAccessFieldsMap(component.getMetadata(), context, nextDepth);

			// check the content
			checkAccessFieldsMap(component.getContent(), context, nextDepth);
		}
	}

	private void checkAccessFieldsMap(Map<String, Field> map,
			RequestContext context, int depth) throws NotAuthorizedException, NotAuthenticatedException {

		if (map != null) {
			int nextDepth = depth - 1;
			List<Field> deleteList = new ArrayList<Field>();
			for (Field field : map.values()) {
				if (field instanceof ComponentLinkField) {
					for (Object linkedComponent : ((ComponentLinkField) field)
							.getValues()) {
						try {
							checkAccessNativeMetadata(
									(Component) linkedComponent, context, false);
							if (linkedComponent instanceof GenericComponent) {
								if (depth > 0) {
									checkAuthorizationGenericComponent(
											(GenericComponent) linkedComponent,
											context, false, nextDepth);
								}
							}
						} catch (FilterOutException e) {
							deleteList.add(field);
						}
					}
				} else if (field instanceof EmbeddedField) {
				    EmbeddedField ef = (EmbeddedField) field;
				    
				    for(FieldSet fs : ef.getEmbeddedValues()){
					checkAccessFieldsMap(
							fs.getContent(),
							context, depth);
				    }
				} else if (field instanceof XhtmlField) {
					checkAccessXhtmlField((XhtmlField) field, context);
				}
			}
			if (!deleteList.isEmpty()) {
				for (Field field : deleteList) {
					map.remove(field.getName());
				}
			}
		}
	}

	// check access for all component links in an xhtml field.
	private void checkAccessXhtmlField(XhtmlField xhtmlField, RequestContext context) throws NotAuthenticatedException {

		List<Object> xhtmlValues = xhtmlField.getValues();
		List<String> newValues = new ArrayList<String>();
		for (Object xhtmlValue : xhtmlValues) {

			// find all component links and try to resolve them
			Pattern p = Pattern.compile("href=\"" + TridionUtils.TCM_REGEX
					+ "\"");
			Matcher m = p.matcher((String) xhtmlValue);

			StringBuffer sb = new StringBuffer();
			String uri = null;
			while (m.find()) {
				uri = m.group(1);
				SimpleComponent component = null;
				try {
					component = (SimpleComponent) simpleComponentFactory
							.getComponent(uri);
					checkAccessNativeMetadata(component, context, false);
				} catch(FilterOutException e){
					// TODO: what should happen here?
					m.appendReplacement(sb, "href=\"\"");					
				} catch (NotAuthorizedException e) {
					// if access denied just empty the href value
					m.appendReplacement(sb, "href=\"\"");
					// TODO: this is different than the td
				} catch (ItemNotFoundException e) {
					// do nothing
				}
			}
			if(sb.length() > 0){
				m.appendTail(sb);
				newValues.add(sb.toString());
			}
		}
		if(!newValues.isEmpty()){
			xhtmlField.setTextValues(newValues);
		}
	}

	private void checkAccessNativeMetadata(PublishedItem item,
			RequestContext context, boolean isRoot)
			throws NotAuthorizedException, FilterOutException, NotAuthenticatedException {

		if (!isInRole(item, context)) {

			// no access!
			if (this.actionIfDenied.equals(ActionIfDenied.Tag)) {
				item.addCustomProperty(ACCESS_DENIED_PROPERTY_NAME,
						Boolean.TRUE);
			} else if (isRoot) {
				if (this.actionIfDenied.equals(ActionIfDenied.Block)
						|| this.actionIfDenied.equals(ActionIfDenied.FilterOut)) {
					throw new NotAuthorizedException("Access denied to item "
							+ item.getId());
				} else {
					logger.error("Unknown actionIfDenied is confiugred: "
							+ this.actionIfDenied);
					throw new RuntimeException(
							"Unknown actionIfDenied configured: "
									+ this.actionIfDenied);
				}
			} else {
				if (this.actionIfDenied.equals(ActionIfDenied.Block)) {
					throw new NotAuthorizedException("Access denied to item "
							+ item.getId());
				} else if (this.actionIfDenied.equals(ActionIfDenied.FilterOut)) {
					throw new FilterOutException();
				} else {
					logger.error("Unknown actionIfDenied is confiugred: "
							+ this.actionIfDenied);
					throw new RuntimeException(
							"Unknown actionIfDenied configured: "
									+ this.actionIfDenied);
				}
			}
		}
	}

	/**
	 * Hacked quickly to update to 2011 - needs more care later. Retrievng the itemMeta through factories
	 * is a waste, caching the item meta (just as the storage meta) is much faster.
	 * 
	 * @param item
	 * @param context
	 * @return
	 * @throws NotAuthenticatedException 
	 */
	private boolean isInRole(PublishedItem item, RequestContext context) throws NotAuthenticatedException {

		if (item != null && context != null) {
		    Category[] categories = null;
		    
		    if(item instanceof Component){
		        ComponentMetaFactory factory = new ComponentMetaFactory(item.getPublication().getId());
		        ComponentMeta meta = factory.getMeta(item.getId());
		        categories = meta.getCategories();
		    }
		    
		    if(item instanceof Page){
		        PageMetaFactory factory = new PageMetaFactory(item.getPublication().getId());
		        PageMeta meta = factory.getMeta(item.getId());
		        categories = meta.getCategories();		        
		    }
		    
		    if(categories != null){
                        // this is needed to avoid problem that the category class is
                        // not loaded.
                        new Category();
                        Category rolesCategory = null;

                        // get all the categories for the item and check if there is a
                        // role category.
                        for (Category category : categories) {
                                if (category.getName().equals(getRoleCategory())) {
                                        rolesCategory = category;
                                        break;
                                }
                        }

                        if (rolesCategory != null) {
                                // loop over all role keywords and check if one of the roles
                                // has access
                                for (Keyword keyword : rolesCategory.getKeywordList()) {
                                        if (context.isUserInRole(keyword.getKeywordName())) {
                                                return true;
                                        }
                                }
                                return false;
                        }		        
		    }
		    else{
		        return false;
		    }
		    
		    /*
			// if a child item does not have any native metadata just retrieve
			// it
			if (item instanceof Component && item.getNativeMetadata() == null) {
				try {
					// get the component without security. Be careful with
					// chaning this
					// since it potentially could cause an infinite loop
					Component comp = simpleComponentFactory.getComponent(
							item.getId());
					item.setNativeMetadata(comp.getNativeMetadata());
				} catch (NotAuthorizedException e) {
					// just let it return true
				} catch (ItemNotFoundException e) {
					// just let it return true
				}
			}

			if (item.getNativeMetadata() != null) {
				// this is needed to avoid problem that the category class is
				// not loaded.
				new Category();
				Category[] categories = item.getNativeMetadata().getCategories();
				Category rolesCategory = null;

				// get all the categories for the item and check if there is a
				// role category.
				for (Category category : categories) {
					if (category.getName().equals(getRoleCategory())) {
						rolesCategory = category;
						break;
					}
				}

				if (rolesCategory != null) {
					// loop over all role keywords and check if one of the roles
					// has access
					for (Keyword keyword : rolesCategory.getKeywordList()) {
						if (context.isUserInRole(keyword.getKeywordName())) {
							return true;
						}
					}
					return false;
				}
			}*/
		}
		// if no security is configured for an item it is public
		return true;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public ActionIfDenied getActionIfDenied() {
		return actionIfDenied;
	}

	public void setActionIfDenied(String actionIfDenied) {
		if ("Block".equals(actionIfDenied)) {
			this.actionIfDenied = ActionIfDenied.Block;
		} else if ("FilterOut".equals(actionIfDenied)) {
			this.actionIfDenied = ActionIfDenied.FilterOut;
		} else if ("Tag".equals(actionIfDenied)) {
			this.actionIfDenied = ActionIfDenied.Tag;
		}
	}

	public void setActionIfDenied(ActionIfDenied actionIfDenied) {
		this.actionIfDenied = actionIfDenied;
	}

	public String getRoleCategory() {
		return roleCategory;
	}

	public void setRoleCategory(String roleCategory) {
		this.roleCategory = roleCategory;
	}
}

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

package org.dd4t.providers.impl;

import com.tridion.broker.StorageException;
import com.tridion.dcp.ComponentPresentation;
import com.tridion.dcp.ComponentPresentationFactory;
import com.tridion.storage.ComponentPresentationMeta;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.ComponentPresentationMetaDAO;
import com.tridion.util.TCMURI;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.impl.TextField;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.providers.BaseBrokerProvider;
import org.dd4t.core.util.Constants;
import org.dd4t.providers.ComponentPresentationProvider;
import org.dd4t.providers.util.DaoUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides access to Dynamic Component Presentations stored in the Content Delivery database. It uses CD API to retrieve
 * raw DCP content from the database. Access to these objects is not cached, and as such must be cached externally.
 */
public class BrokerComponentPresentationProvider extends BaseBrokerProvider implements ComponentPresentationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BrokerComponentPresentationProvider.class);
	private static final Map<Integer, ComponentPresentationFactory> FACTORY_CACHE = new ConcurrentHashMap<>();
	private static final String ERROR_MESSAGE = "Component Presentation not found for componentId: %d, templateId: %d and publicationId: %d";

	private Class<? extends org.dd4t.contentmodel.ComponentPresentation> concreteComponentPresentation;
	private Class<? extends ComponentTemplate> concreteComponentTemplateImpl;


	/**
	 * Retrieves content of a Dynamic Component Presentation by looking up its componentId and publicationId.
	 * A templateId is not provided, so the DCP with the highest linking priority is retrieved.
	 * <p/>
	 * <b>Note: This method performs significantly slower than getDynamicComponentPresentation(int, int, int)!
	 * Do provide a templateId!</b>
	 *
	 * @param componentId   int representing the Component item id
	 * @param publicationId int representing the Publication id of the DCP
	 * @return String representing the content of the DCP
	 * @throws ItemNotFoundException if the requested DCP cannot be found
	 */
	@Override public String getDynamicComponentPresentation (int componentId, int publicationId) throws ItemNotFoundException, SerializationException {
		return getDynamicComponentPresentation(componentId, 0, publicationId);
	}

	/**
	 * Retrieves content of a Dynamic Component Presentation by looking up its componentId, templateId and publicationId.
	 *
	 * @param componentId   int representing the Component item id
	 * @param templateId    int representing the Component Template item id
	 * @param publicationId int representing the Publication id of the DCP
	 * @return String representing the content of the DCP
	 * @throws ItemNotFoundException if the requested DCP cannot be found
	 */
	@Override public String getDynamicComponentPresentation (int componentId, int templateId, int publicationId) throws ItemNotFoundException, SerializationException {
		ComponentPresentationFactory factory = FACTORY_CACHE.get(publicationId);

		if (factory == null) {
			factory = new ComponentPresentationFactory(publicationId);
			FACTORY_CACHE.put(publicationId, factory);
		}

		ComponentPresentation result;
		String resultString;
		if (templateId != 0) {
			result = factory.getComponentPresentation(componentId, templateId);

			if (result == null) {
				LOG.info(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
				throw new ItemNotFoundException(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
			}

			resultString = result.getContent();
		} else {
			result = factory.getComponentPresentationWithHighestPriority(componentId);
			if (result == null) {
				LOG.info(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
				throw new ItemNotFoundException(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
			}

			resultString = result.getContent();
		}

		if (!StringUtils.isEmpty(resultString)) {
			return decodeAndDecompressContent(resultString);
		}
		return null;
	}

	/**
	 * Convenience method to obtain a list of component presentations for the same template id.
	 *
	 * @param itemUris      array of found Component TCM IDs
	 * @param templateId    the CT Id to fetch DCPs on
	 * @param publicationId the current Publication Id
	 * @return a List of Component Presentations
	 * @throws org.dd4t.core.exceptions.ItemNotFoundException
	 * @throws org.dd4t.core.exceptions.SerializationException
	 */
	@Override public List<String> getDynamicComponentPresentations (final String[] itemUris, final int templateId, final int publicationId) throws ItemNotFoundException, SerializationException {
		List<String> componentPresentations = new ArrayList<>();

		for (String itemUri : itemUris) {
			try {
				org.dd4t.core.util.TCMURI uri = new org.dd4t.core.util.TCMURI(itemUri);
				componentPresentations.add(getDynamicComponentPresentation(uri.getItemId(),templateId,publicationId));
			} catch (ParseException e) {
				throw new SerializationException(e);
			}
		}
		return componentPresentations;
	}

	// TODO Remove after testing
	@Deprecated
	private org.dd4t.contentmodel.ComponentPresentation constructComponentPresentation (String componentSource, int publicationId, int componentId, int componentTemplateId, ComponentPresentation componentPresentation) {
		try {

			final ComponentPresentationMetaDAO componentPresentationMetaDAO = (ComponentPresentationMetaDAO) DaoUtils.getStorageDAO(publicationId, StorageTypeMapping.COMPONENT_PRESENTATION_META);
			final ComponentPresentationMeta componentPresentationMeta = componentPresentationMetaDAO.findByPrimaryKey(publicationId, componentId, componentTemplateId);

			final org.dd4t.contentmodel.ComponentPresentation componentPresentationResult = this.concreteComponentPresentation.newInstance();
			final ComponentTemplate componentTemplate = this.concreteComponentTemplateImpl.newInstance();
			componentPresentationResult.setRawComponentContent(componentSource);
			componentPresentationResult.setIsDynamic(componentPresentation.isDynamic());
			componentTemplate.setId(new TCMURI(publicationId, componentTemplateId, 32, 0).toString());
			componentTemplate.setTitle(componentPresentationMeta.getTemplateMeta().getTitle());
			final DateTime dateTime = new DateTime(componentPresentationMeta.getTemplateMeta().getLastPublishDate());
			componentTemplate.setRevisionDate(dateTime);
			final Map<String, Field> metadata = new HashMap<>();

			// TODO: this is a hack
			// Component Template Custom Meta is not published with
			// the component template, so we cannot read the viewName.
			// Therefore, the only supported way for now is use the lower cased
			// template name as view model name...

			// We should actually fix this in the Generate Dynamic Component TBB to also
			// include CT data.

			final List<String> values = new ArrayList<>();
			values.add(stringToDashCase(componentTemplate.getTitle()));

			TextField field = new TextField();
			field.setName(Constants.VIEW_NAME_FIELD);
			field.setTextValues(values);
			metadata.put(Constants.VIEW_NAME_FIELD, field);

			componentTemplate.setMetadata(metadata);
			componentPresentationResult.setComponentTemplate(componentTemplate);
			return componentPresentationResult;
		} catch (InstantiationException | StorageException | IllegalAccessException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	// TODO: move away from this.
	/**
	 * Utility method to fix a constant value (probably a CMS-able value from
	 * Tridion), so it can be used inside a URL: lower case and all spaces and
	 * underscores are replaced by dashes (-).
	 */
	public static String stringToDashCase (String value) {
		if (value == null) {
			return "";
		}
		return value.replaceAll("[^a-zA-Z0-9]", "_").replaceAll("([_]+)", "_").toLowerCase();
	}
// TODO Remove after testing

	public void setConcreteComponentPresentation (final Class<? extends org.dd4t.contentmodel.ComponentPresentation> concreteComponentPresentation) {
		this.concreteComponentPresentation = concreteComponentPresentation;
	}
	// TODO Remove after testing
	public void setConcreteComponentTemplateImpl (final Class<? extends ComponentTemplate> concreteComponentTemplateImpl) {
		this.concreteComponentTemplateImpl = concreteComponentTemplateImpl;
	}
}

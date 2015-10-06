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

package org.dd4t.providers.rs;

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.impl.TextField;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.Constants;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.ComponentPresentationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Component provider.
 */
public class BrokerComponentPresentationProvider extends BaseBrokerProvider implements ComponentPresentationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BrokerComponentPresentationProvider.class);
	private Class<? extends org.dd4t.contentmodel.ComponentPresentation> concreteComponentPresentation;
	private Class<? extends ComponentTemplate> concreteComponentTemplateImpl;

	public BrokerComponentPresentationProvider () {
		LOG.debug("Create new instance");
	}




	/**
	 * Retrieves content of a Dynamic Component Presentation by looking up its componentId and publicationId.
	 * A templateId is not provided, so the DCP with the highest linking priority is retrieved.
	 * The returned content represents a JSON encoded string.
	 * <p/>
	 * <b>Note: This method performs significantly slower than getDynamicComponentPresentation(int, int, int)!
	 * Do provide a templateId!</b>
	 *
	 * @param componentId   int representing the Component item id
	 * @param publicationId int representing the Publication id of the DCP
	 * @return String representing the content of the DCP
	 * @throws ItemNotFoundException  if the requested DCP does not exist
	 * @throws SerializationException if something went wrong during deserialization
	 */
	public String getFullDynamicComponentPresentation (final int componentId, final int publicationId) throws ItemNotFoundException, SerializationException {
		return getFullDynamicComponentPresentation(componentId, 0, publicationId);
	}

	/**
	 * Retrieves content of a Dynamic Component Presentation by looking up its componentId, templateId and publicationId.
	 * The returned content represents a JSON encoded string.
	 * TODO: rework
	 * @param componentId   int representing the Component item id
	 * @param templateId    int representing the Component Template item id
	 * @param publicationId int representing the Publication id of the DCP
	 * @return String representing the content of the DCP
	 * @throws ItemNotFoundException  if the requested DCP does not exist
	 * @throws SerializationException if something went wrong during deserialization
	 */
	public String getFullDynamicComponentPresentation (final int componentId, final int templateId, final int publicationId) throws ItemNotFoundException, SerializationException {

		LOG.debug("Fetching Component Presentation by componentId: {}, templateId: {} and publicationId: {}", new Object[]{componentId, templateId, publicationId});

		String publication = String.valueOf(publicationId);
		String template = String.valueOf(templateId);
		String component = String.valueOf(componentId);

		try {
			Invocation.Builder builder = client.getComponentByIdTarget().
					path(publication).path(template).path(component).request(MediaType.TEXT_PLAIN);
			builder = getSessionPreviewBuilder(builder);

			String content = builder.get(String.class);
			if (content == null || content.length() == 0) {
				throw new ItemNotFoundException(String.format("Cannot find Component Presentation for componentId: %s, templateId: %s and publicationId: %s", component, template, publication));
			}

			return decodeAndDecompressContent(content);
		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}

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
	 * @throws org.dd4t.core.exceptions.ItemNotFoundException  if the requested DCP does not exist
	 * @throws org.dd4t.core.exceptions.SerializationException if something went wrong during deserialization
	 */
	@Override public ComponentPresentation getDynamicComponentPresentation (final int componentId, final int publicationId) throws ItemNotFoundException, SerializationException {
		return null;
	}

	/**
	 * Retrieves content of a Dynamic Component Presentation by looking up its componentId, templateId and publicationId.
	 *
	 * @param componentId   int representing the Component item id
	 * @param templateId    int representing the Component Template item id
	 * @param publicationId int representing the Publication id of the DCP
	 * @return String representing the content of the DCP
	 * @throws org.dd4t.core.exceptions.ItemNotFoundException  if the requested DCP does not exist
	 * @throws org.dd4t.core.exceptions.SerializationException if something went wrong during deserialization
	 */
	@Override public ComponentPresentation getDynamicComponentPresentation (final int componentId, final int templateId, final int publicationId) throws ItemNotFoundException, SerializationException {


		return constructComponentPresentation(getFullDynamicComponentPresentation(componentId, templateId, publicationId),publicationId,componentId,templateId);
	}


	private org.dd4t.contentmodel.ComponentPresentation constructComponentPresentation (String componentSource, int publicationId, int componentId, int componentTemplateId) {
		try {

			// TODO: with the dd4t-2 DCP TBBs this is now not necessary anymore. Return a simple String with teh content!
//			final ComponentPresentationMetaDAO componentPresentationMetaDAO = (ComponentPresentationMetaDAO) DaoUtils.getStorageDAO(publicationId, StorageTypeMapping.COMPONENT_PRESENTATION_META);
//			final ComponentPresentationMeta componentPresentationMeta = componentPresentationMetaDAO.findByPrimaryKey(publicationId, componentId, componentTemplateId);

			final org.dd4t.contentmodel.ComponentPresentation componentPresentationResult = this.concreteComponentPresentation.newInstance();
			final ComponentTemplate componentTemplate = this.concreteComponentTemplateImpl.newInstance();
			componentPresentationResult.setRawComponentContent(componentSource);

			componentPresentationResult.setIsDynamic(true);
			componentTemplate.setId(new TCMURI(publicationId, componentTemplateId, 32, 0).toString());
			componentTemplate.setTitle("Event");
//			final DateTime dateTime = new DateTime(componentPresentationMeta.getTemplateMeta().getLastPublishDate());
//			componentTemplate.setRevisionDate(dateTime);
			final Map<String, Field> metadata = new HashMap<>();

			// TODO: this is a hack - Update: can now be removed!
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
		} catch (InstantiationException  | IllegalAccessException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	public static String stringToDashCase (String value) {
		if (value == null) {
			return "";
		}
		return value.replaceAll("[^a-zA-Z0-9]", "_").replaceAll("([_]+)", "_").toLowerCase();
	}

	/**
	 * Convenience method to obtain a list of component presentations for the same template id.
	 * TODO
	 *
	 * @param itemUris      array of found Component TCM IDs
	 * @param templateId    the CT Id to fetch DCPs on
	 * @param publicationId the current Publication Id
	 * @return a List of Component Presentations
	 * @throws org.dd4t.core.exceptions.ItemNotFoundException
	 * @throws org.dd4t.core.exceptions.SerializationException
	 */
	@Override public List<ComponentPresentation> getDynamicComponentPresentations (final String[] itemUris, final int templateId, final int publicationId) throws ItemNotFoundException, SerializationException {
		return new ArrayList<>();
	}

	public Class<? extends ComponentPresentation> getConcreteComponentPresentation () {
		return concreteComponentPresentation;
	}

	public void setConcreteComponentPresentation (final Class<? extends ComponentPresentation> concreteComponentPresentation) {
		this.concreteComponentPresentation = concreteComponentPresentation;
	}

	public Class<? extends ComponentTemplate> getConcreteComponentTemplateImpl () {
		return concreteComponentTemplateImpl;
	}

	public void setConcreteComponentTemplateImpl (final Class<? extends ComponentTemplate> concreteComponentTemplateImpl) {
		this.concreteComponentTemplateImpl = concreteComponentTemplateImpl;
	}
}

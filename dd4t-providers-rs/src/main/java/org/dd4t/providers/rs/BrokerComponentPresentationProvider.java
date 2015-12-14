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

import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.providers.ComponentPresentationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Component provider.
 */
public class BrokerComponentPresentationProvider extends BaseBrokerProvider implements ComponentPresentationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BrokerComponentPresentationProvider.class);

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
	@Override public String getDynamicComponentPresentation (final int componentId, final int publicationId) throws ItemNotFoundException, SerializationException {
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
	@Override public String getDynamicComponentPresentation (final int componentId, final int templateId, final int publicationId) throws ItemNotFoundException, SerializationException {
		return null;
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
	@Override public List<String> getDynamicComponentPresentations (final String[] itemUris, final int templateId, final int publicationId) throws ItemNotFoundException, SerializationException {
		return new ArrayList<>();
	}
}

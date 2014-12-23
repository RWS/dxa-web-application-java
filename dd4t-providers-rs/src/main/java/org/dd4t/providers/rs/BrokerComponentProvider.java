package org.dd4t.providers.rs;

import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.providers.ComponentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;

/**
 * Component provider.
 */
public class BrokerComponentProvider extends BaseBrokerProvider implements ComponentProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BrokerComponentProvider.class);

	public BrokerComponentProvider () {
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
	@Override
	public String getDynamicComponentPresentation (final int componentId, final int publicationId) throws ItemNotFoundException, SerializationException {
		return getDynamicComponentPresentation(componentId, 0, publicationId);
	}

	/**
	 * Retrieves content of a Dynamic Component Presentation by looking up its componentId, templateId and publicationId.
	 * The returned content represents a JSON encoded string.
	 *
	 * @param componentId   int representing the Component item id
	 * @param templateId    int representing the Component Template item id
	 * @param publicationId int representing the Publication id of the DCP
	 * @return String representing the content of the DCP
	 * @throws ItemNotFoundException  if the requested DCP does not exist
	 * @throws SerializationException if something went wrong during deserialization
	 */
	@Override
	public String getDynamicComponentPresentation (final int componentId, final int templateId, final int publicationId) throws ItemNotFoundException, SerializationException {

		LOG.debug("Fetching Component Presentation by componentId: {}, templateId: {} and publicationId: {}", new Object[]{componentId, templateId, publicationId});

		String publication = String.valueOf(publicationId);
		String template = String.valueOf(templateId);
		String component = String.valueOf(componentId);

		try {
			Invocation.Builder builder = JAXRSClient.INSTANCE.getComponentByIdTarget().
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
}

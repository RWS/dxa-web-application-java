package org.dd4t.providers;

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;

import java.util.List;

/**
 * Component Provider.
 */
public interface ComponentPresentationProvider {

    /**
     * Retrieves content of a Dynamic Component Presentation by looking up its componentId and publicationId.
     * A templateId is not provided, so the DCP with the highest linking priority is retrieved.
	 *
     * <b>Note: This method performs significantly slower than getDynamicComponentPresentation(int, int, int)!
     * Do provide a templateId!</b>
     *
     * @param componentId   int representing the Component item id
     * @param publicationId int representing the Publication id of the DCP
     * @return String representing the content of the DCP
     * @throws ItemNotFoundException  if the requested DCP does not exist
     * @throws SerializationException if something went wrong during deserialization
     */
    public org.dd4t.contentmodel.ComponentPresentation getDynamicComponentPresentation(int componentId, int publicationId) throws ItemNotFoundException, SerializationException;

    /**
     * Retrieves content of a Dynamic Component Presentation by looking up its componentId, templateId and publicationId.
     *
     * @param componentId   int representing the Component item id
     * @param templateId    int representing the Component Template item id
     * @param publicationId int representing the Publication id of the DCP
     * @return String representing the content of the DCP
     * @throws ItemNotFoundException  if the requested DCP does not exist
     * @throws SerializationException if something went wrong during deserialization
     */
    public org.dd4t.contentmodel.ComponentPresentation getDynamicComponentPresentation(int componentId, int templateId, int publicationId) throws ItemNotFoundException, SerializationException;

	/**
	 * Convenience method to obtain a list of component presentations for the same template id.
	 *
	 * @param itemUris array of found Component TCM IDs
	 * @param templateId the CT Id to fetch DCPs on
	 * @param publicationId the current Publication Id
	 * @return a List of Component Presentations
	 * @throws ItemNotFoundException
	 * @throws SerializationException
	 */
	public List<ComponentPresentation> getDynamicComponentPresentations(String[] itemUris, int templateId, int publicationId) throws ItemNotFoundException, SerializationException;
}

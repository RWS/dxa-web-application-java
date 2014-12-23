package org.dd4t.providers;

import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;

/**
 * Component Provider.
 */
public interface ComponentProvider {

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
    public String getDynamicComponentPresentation(int componentId, int publicationId) throws ItemNotFoundException, SerializationException;

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
    public String getDynamicComponentPresentation(int componentId, int templateId, int publicationId) throws ItemNotFoundException, SerializationException;
}

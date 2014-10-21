package org.dd4t.providers;

import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.core.request.RequestContext;

/**
 * Client side of the Component provider. This communicates with the service layer (Hammerfest) to read
 * Dynamic Component Presentations by Component id, Component Template id and Publication id.
 * The communication is done using JAX-RS singleton client.
 * <p/>
 * No Tridion dependencies are allowed here (they are all in Hammerfest)
 */
public interface ComponentProvider {

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
     * @param context       RequestContext object providing access to HttpServletRequest
     * @return String representing the content of the DCP
     * @throws ItemNotFoundException  if the requested DCP does not exist
     * @throws SerializationException if something went wrong during deserialization
     */
    public String getDynamicComponentPresentation(int componentId, int publicationId) throws ItemNotFoundException, SerializationException;

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
    public String getDynamicComponentPresentation(int componentId, int templateId, int publicationId) throws ItemNotFoundException, SerializationException;
}

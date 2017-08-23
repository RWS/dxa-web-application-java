package org.dd4t.providers;

import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public abstract class AbstractComponentPresentationProvider extends BaseBrokerProvider implements ComponentPresentationProvider {

    protected static final String ERROR_MESSAGE = "Component Presentation not found for componentId: %d, templateId: %d and publicationId: %d";

    @Override
    public abstract String getDynamicComponentPresentation(int itemId, int templateId, int publicationId)  throws ItemNotFoundException, SerializationException;

    /**
     * Convenience method to obtain a list of component presentations for the same template id.
     *
     * @param itemUris      array of found Component TCM_ZERO_URI IDs
     * @param templateId    the CT Id to fetch DCPs on
     * @param publicationId the current Publication Id
     * @return a List of Component Presentations
     * @throws ItemNotFoundException
     * @throws SerializationException
     */
    @Override
    public List<String> getDynamicComponentPresentations (final String[] itemUris, final int templateId, final int publicationId) throws ItemNotFoundException, SerializationException {
        List<String> componentPresentations = new ArrayList<>();

        for (String itemUri : itemUris) {
            try {
                org.dd4t.core.util.TCMURI uri = new org.dd4t.core.util.TCMURI(itemUri);
                componentPresentations.add(getDynamicComponentPresentation(uri.getItemId(), templateId, publicationId));
            } catch (ParseException e) {
                throw new SerializationException(e);
            }
        }
        return componentPresentations;
    }

    protected static void assertQueryResultNotNull (Object result, int componentId, int templateId, int publicationId) throws ItemNotFoundException {
        if (result == null) {
            throw new ItemNotFoundException(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
        }
    }
}

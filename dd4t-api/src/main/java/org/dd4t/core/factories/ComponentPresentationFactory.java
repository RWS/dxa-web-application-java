package org.dd4t.core.factories;

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.core.exceptions.FactoryException;

// TODO: expand with: getComponentPresentations
public interface ComponentPresentationFactory extends Factory {

    /**
     * Get a component by its uri and Component Template URI.
     *
     * @param componentURI      String representing the Component TCMURI to retrieve
     * @param viewOrTemplateURI String representing either the View Name or Component Template TCMURI
     *                          to use when looking up the DCP
     * @return a Generic Component object
     * @throws org.dd4t.core.exceptions.FactoryException
     */
    public ComponentPresentation getComponentPresentation (String componentURI, String viewOrTemplateURI) throws FactoryException;
}

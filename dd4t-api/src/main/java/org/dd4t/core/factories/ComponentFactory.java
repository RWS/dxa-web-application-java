package org.dd4t.core.factories;

import org.dd4t.contentmodel.Component;
import org.dd4t.core.exceptions.FactoryException;

public interface ComponentFactory extends Factory {

    /**
     * Get a component by its uri and Component Template URI.
     *
     * @param componentURI      String representing the Component TCMURI to retrieve
     * @param viewOrTemplateURI String representing either the View Name or Component Template TCMURI
     *                          to use when looking up the DCP
     * @return a Generic Component object
     * @throws org.dd4t.core.exceptions.FactoryException
     */
    public Component getComponent(String componentURI, String viewOrTemplateURI) throws FactoryException;

    public <T extends Component> T deserialize (final String componentModel, final Class<? extends T> componentClass) throws FactoryException;
}

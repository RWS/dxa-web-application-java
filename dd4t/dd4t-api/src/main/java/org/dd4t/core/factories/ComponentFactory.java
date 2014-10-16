package org.dd4t.core.factories;

import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.core.filters.FilterException;
import org.dd4t.core.request.RequestContext;

import java.text.ParseException;

public interface ComponentFactory extends Factory {

    /**
     * Get a component by its uri and Component Template URI.
     *
     * @param componentURI      String representing the Component TCMURI to retrieve
     * @param viewOrTemplateURI String representing either the View Name or Component Template TCMURI
     *                          to use when looking up the DCP
     * @return
     * @throws ItemNotFoundException
     */
    public GenericComponent getComponent(String componentURI, String viewOrTemplateURI)
            throws ItemNotFoundException, ParseException, FilterException, SerializationException;

    /**
     * Get a component by its uri and Component Template URI.
     *
     * @param componentURI      String representing the Component TCMURI to retrieve
     * @param viewOrTemplateURI String representing either the View Name or Component Template TCMURI
     *                          to use when looking up the DCP
     * @param context           RequestContext object providing access to HttpServletRequest
     * @return
     * @throws ItemNotFoundException
     */
    public GenericComponent getComponent(String componentURI, String viewOrTemplateURI, RequestContext context)
            throws ItemNotFoundException, FilterException, ParseException, SerializationException;
}

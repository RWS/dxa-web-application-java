package org.dd4t.core.factories;

import org.dd4t.core.services.LabelService;

/**
 *
 * TODO: should this be in the API?
 *
 * @author Mihai Cadariu
 */
public interface LabelServiceFactory {

    public LabelService getLabelService();
}

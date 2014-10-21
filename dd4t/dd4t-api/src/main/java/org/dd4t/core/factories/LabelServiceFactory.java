package org.dd4t.core.factories;

import org.dd4t.core.services.LabelService;

/**
 * @author Mihai Cadariu
 * @since 21.07.2014
 */
public interface LabelServiceFactory {

    public LabelService getLabelService();
}

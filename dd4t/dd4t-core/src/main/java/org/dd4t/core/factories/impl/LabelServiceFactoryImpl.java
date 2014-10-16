package org.dd4t.core.factories.impl;

import org.dd4t.core.factories.LabelServiceFactory;
import org.dd4t.core.services.LabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * LabelServiceFactoryImpl hides actual LabelService implementation
 * <p/>
 * Configured through Spring beans
 *
 * @author Mihai Cadariu
 * @since 21.07.2014
 */
public class LabelServiceFactoryImpl implements LabelServiceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(LabelServiceFactoryImpl.class);
    private static final LabelServiceFactory _instance = new LabelServiceFactoryImpl();
    private LabelService labelService;

    private LabelServiceFactoryImpl() {
        LOG.debug("Create new instance");
    }

    public static LabelServiceFactory getInstance() {
        return _instance;
    }

    @Override
    public LabelService getLabelService() {
        return labelService;
    }

    @Autowired
    private void setLabelService(LabelService labelService) {
        LOG.debug("Set LabelService " + labelService);
        this.labelService = labelService;
    }
}

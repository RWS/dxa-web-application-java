package org.dd4t.core.services.impl;

import org.dd4t.core.services.PropertiesService;

import java.util.Properties;

/**
 * @author Mihai Cadariu
 * @since 18.07.2014
 */
public abstract class PropertiesServiceBase implements PropertiesService {

    protected Properties properties;

    @Override
    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    @Override
    public String getProperty(String name, String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }
}

package org.dd4t.core.services;

/**
 * TODO: determine whether this should be in the API
 *
 * @author Mihai Cadariu
 */
public interface PropertiesService {

    public void load(String propertiesFile);

    public String getProperty(String name);

    public String getProperty(String name, String defaultValue);
}

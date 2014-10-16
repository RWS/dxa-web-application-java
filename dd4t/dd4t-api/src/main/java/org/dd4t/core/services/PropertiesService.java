package org.dd4t.core.services;

/**
 * @author Mihai Cadariu
 * @since 18.07.2014
 */
public interface PropertiesService {

    public void load(String propertiesFile);

    public String getProperty(String name);

    public String getProperty(String name, String defaultValue);
}

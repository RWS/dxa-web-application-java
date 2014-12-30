package org.dd4t.core.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * dd4t-2
 *
 * @author M.Cadariu
 */
public class PropertiesServiceImpl extends PropertiesServiceBase {

	private static final Logger LOG = LoggerFactory.getLogger(PropertiesServiceImpl.class);

	private PropertiesServiceImpl () {
	}

	@Override public void load (String propertiesFile) {
		LOG.debug("Loading file " + propertiesFile);
		try {
			InputStream input = PropertiesServiceImpl.class.getClassLoader().getResourceAsStream(propertiesFile);
			if (input == null) {
				throw new IOException("Cannot find properties file '" + propertiesFile + "' in classpath");
			}

			properties = new Properties();
			properties.load(input);
		} catch (IOException ioe) {
			LOG.error("Failed to load properties file " + propertiesFile, ioe);
		}
	}

	@Required public void setLocation (String location) {
		LOG.debug("Load Properties from: {}", location);
		load(location);
	}
}
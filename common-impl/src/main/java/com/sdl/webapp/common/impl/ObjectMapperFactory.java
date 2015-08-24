package com.sdl.webapp.common.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Object mapper factory. This factory bean creates and configures the Jackson {@code ObjectMapper}.
 */
@Component
public class ObjectMapperFactory extends AbstractFactoryBean<ObjectMapper> {

    @Override
    public Class<?> getObjectType() {
        return ObjectMapper.class;
    }

    @Override
    protected ObjectMapper createInstance() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        // Make it find and register Jackson modules, for example to add support for Joda Time objects
        objectMapper.findAndRegisterModules();

        return objectMapper;
    }
}

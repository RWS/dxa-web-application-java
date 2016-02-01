package com.sdl.webapp.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
/**
 * <p>ApplicationContextHolder class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;

    /**
     * <p>Getter for the field <code>context</code>.</p>
     *
     * @return a {@link org.springframework.context.ApplicationContext} object.
     */
    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}

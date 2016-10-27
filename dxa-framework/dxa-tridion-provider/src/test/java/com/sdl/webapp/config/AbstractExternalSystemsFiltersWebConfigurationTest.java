package com.sdl.webapp.config;

import org.junit.Test;

import javax.servlet.ServletContext;

import static org.junit.Assert.assertEquals;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

public class AbstractExternalSystemsFiltersWebConfigurationTest {

    @Test
    public void shouldBeHighestPrecedenceBecauseOfADF() {
        assertEquals(HIGHEST_PRECEDENCE, new AbstractExternalSystemsFiltersWebConfiguration() {
            @Override
            protected void registerXpm(ServletContext servletContext) {
            }

            @Override
            protected void registerAdf(ServletContext servletContext) {
            }
        }.getOrder());
    }

}
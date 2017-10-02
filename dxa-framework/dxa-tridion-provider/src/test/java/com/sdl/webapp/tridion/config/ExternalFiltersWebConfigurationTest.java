package com.sdl.webapp.tridion.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

public class ExternalFiltersWebConfigurationTest {

    @Test
    public void shouldBeHighestPrecedenceBecauseOfADF() {
        assertEquals(HIGHEST_PRECEDENCE, new ExternalFiltersWebConfiguration().getOrder());
    }
}
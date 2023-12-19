package com.sdl.webapp.tridion.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

public class ExternalFiltersWebConfigurationTest {

    @Test
    public void shouldBeHighestPrecedenceBecauseOfADF() {
        assertEquals(HIGHEST_PRECEDENCE, new ExternalFiltersWebConfiguration().getOrder());
    }
}
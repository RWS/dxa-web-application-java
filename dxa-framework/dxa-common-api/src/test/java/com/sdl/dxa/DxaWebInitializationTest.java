package com.sdl.dxa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

public class DxaWebInitializationTest {

    @Test
    public void shouldHaveHighestPrecedenceOrder() throws Exception {
        assertEquals(HIGHEST_PRECEDENCE, new DxaWebInitialization().getOrder());
    }

}
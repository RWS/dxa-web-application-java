package com.sdl.dxa;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

public class DxaWebInitializationTest {

    @Test
    public void shouldHaveHighestPrecedenceOrder() throws Exception {
        assertEquals(HIGHEST_PRECEDENCE, new DxaWebInitialization().getOrder());
    }

}
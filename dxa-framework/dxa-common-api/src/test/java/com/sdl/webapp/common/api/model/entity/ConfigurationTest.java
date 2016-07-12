package com.sdl.webapp.common.api.model.entity;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ConfigurationTest {

    @Test
    public void shouldReturnConfiguration() {
        //given
        Configuration configuration = new Configuration();
        Map<String, String> settings = new HashMap<>();
        settings.put("key1", "value1");
        settings.put("key2", "value2");

        //when
        configuration.setSettings(settings);

        //then
        assertEquals(settings, configuration.getSettings());
        assertEquals("Configuration(" +
                     "settings=" + settings.toString() +
                     ")",
                     configuration.toString());
    }
}
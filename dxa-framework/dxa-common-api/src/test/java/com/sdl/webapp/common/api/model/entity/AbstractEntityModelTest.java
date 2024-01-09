package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.DxaSpringInitialization;
import com.sdl.webapp.common.util.ApplicationContextHolder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class AbstractEntityModelTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldGenerateCorrectMarkupForXPM() throws Exception {
        //given
        final HashMap<String, Object> xpmMetadata = new HashMap<String, Object>() {
            {
                put("Boolean", String.valueOf(true));
                put("Boolean_2", true);
            }
        };
        AbstractEntityModel model = new AbstractEntityModel() {
            {

                setXpmMetadata(xpmMetadata);
            }
        };

        //when
        String xpmMarkup = model.getXpmMarkup(null);

        //then
        assertTrue(xpmMarkup.startsWith("<!-- Start Component Presentation:"));
        xpmMarkup = xpmMarkup.replace("<!-- Start Component Presentation:", "");
        assertTrue(xpmMarkup.endsWith("-->"));
        xpmMarkup = xpmMarkup.replace("-->", "");
        assertEquals(xpmMetadata, objectMapper.readValue(xpmMarkup, Map.class));
    }

    @Test
    public void shouldAddExtensionData() {
        //given
        AbstractEntityModel entityModel = new AbstractEntityModel() {
        };

        //when
        entityModel.addExtensionData("key", "value");

        //then
        assertEquals("value", entityModel.getExtensionData().get("key"));
    }

    @Test
    public void shouldSetExtensionData() {
        //given
        AbstractEntityModel entityModel = new AbstractEntityModel() {
        };
        HashMap<String, Object> extensionData = new HashMap<String, Object>() {{
            put("key", "value");
        }};

        //when
        entityModel.setExtensionData(extensionData);

        //then
        assertEquals("value", entityModel.getExtensionData().get("key"));
    }

    @org.springframework.context.annotation.Configuration
    @Profile("test")
    static class SpringConfig {

        @Bean
        public ObjectMapper objectMapper() {
            return new DxaSpringInitialization().objectMapper();
        }

        @Bean
        public ApplicationContextHolder applicationContextHolder() {
            return new ApplicationContextHolder();
        }
    }

}
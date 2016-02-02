package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
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
        System.out.println(objectMapper.writeValueAsString(xpmMetadata));

    }

    @SuppressWarnings("Duplicates")
    @org.springframework.context.annotation.Configuration
    static class SpringConfig {
        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.registerModule(new JodaModule());
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            return objectMapper;
        }

        @Bean
        public ApplicationContextHolder applicationContextHolder() {
            return new ApplicationContextHolder();
        }
    }

}
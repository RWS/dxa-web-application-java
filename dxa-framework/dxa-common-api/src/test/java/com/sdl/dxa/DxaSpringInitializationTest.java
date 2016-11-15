package com.sdl.dxa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.webapp.common.api.contextengine.ContextEngine;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import com.sdl.webapp.common.util.InitializationUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class DxaSpringInitializationTest {

    @Autowired
    private ViewResolver dxaViewResolver;

    @Test
    public void shouldFindOverriddenView() throws Exception {
        //when
        View view = dxaViewResolver.resolveViewName("Override", Locale.getDefault());

        //then
        assertNotNull(view);
        assertTrue(view.toString().contains("Override.android.jsp"));
    }

    @Test
    public void shouldDetectDeviceFamilyAndChangeViewName() throws Exception {
        //when
        View view = dxaViewResolver.resolveViewName("TestView", Locale.getDefault());

        //then
        assertNotNull(view);
        assertTrue(view.toString().contains("TestView.android.jsp"));
    }

    @Test
    public void shouldDetectRedirectOrForwardRequests() throws Exception {
        //given 

        //when
        View view = dxaViewResolver.resolveViewName("redirect:RedirectTestView", Locale.getDefault());
        View view2 = dxaViewResolver.resolveViewName("forward:ForwardTestView", Locale.getDefault());

        //then
        assertTrue(view instanceof RedirectView);
        assertTrue(view2 instanceof InternalResourceView);
        assertEquals("RedirectTestView", ((RedirectView) view).getUrl());
        assertEquals("ForwardTestView", ((InternalResourceView) view2).getUrl());
    }


    @Test
    public void shouldSerializeDatesUsingISO_8601() throws IOException {
        //given
        ObjectMapper objectMapper = new DxaSpringInitialization().objectMapper();
        long timestamp = 42000;
        String example = "{\"date\" : \"1970-01-01T00:00:42.000+0000\", " +
                "\"dateZ\" : \"1970-01-01T00:00:42.000Z\"," +
                "\"dateTime\" : \"1970-01-01T00:00:42.000Z\"," +
                "\"dateTimeFull\" : \"1970-01-01T00:00:42.000+0000\"}";

        //when
        DateTest exampleDate = objectMapper.readValue(example, DateTest.class);
        String serializedDate = objectMapper.writeValueAsString(new DateTest(timestamp));

        //then
        assertEquals(exampleDate, objectMapper.readValue(serializedDate, DateTest.class));
    }

    @Data
    @NoArgsConstructor
    public static class DateTest {

        private Date date;

        private Date dateZ;

        private DateTime dateTime;

        private DateTime dateTimeFull;

        public DateTest(long timestamp) {
            this.date = new Date(timestamp);
            this.dateZ = new Date(timestamp);
            this.dateTime = new DateTime(timestamp);
            this.dateTimeFull = new DateTime(timestamp);
        }
    }

    @Configuration
    @Profile("test")
    public static class TestContextConfiguration {

        @Bean
        public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
            PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
            configurer.setProperties(InitializationUtils.loadDxaProperties());
            return configurer;
        }

        @Bean
        public ApplicationContextHolder applicationContextHolder() {
            return new ApplicationContextHolder();
        }

        @Bean
        public ViewResolver dxaViewResolver() {
            return dxaSpringInitialization().dxaViewResolver();
        }

        @Bean
        public DxaSpringInitialization dxaSpringInitialization() {
            return new DxaSpringInitialization();
        }

        @Bean
        public ContextEngine contextEngine() {
            ContextEngine contextEngine = mock(ContextEngine.class);
            when(contextEngine.getDeviceFamily()).thenReturn("android");
            return contextEngine;
        }
    }

}
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
@ContextConfiguration(classes = DxaSpringInitializationTest.TestContextConfiguration.class)
@ActiveProfiles("test")
public class DxaSpringInitializationTest {

    @Autowired
    @Qualifier("overrideDeviceContextualViewResolver")
    private ViewResolver overrideDeviceContextualViewResolver;

    @Autowired
    @Qualifier("deviceContextualViewResolver")
    private ViewResolver deviceContextualViewResolver;

    @Autowired
    @Qualifier("forwardRedirectViewResolver")
    private ViewResolver forwardRedirectViewResolver;

    @Autowired
    @Qualifier("fallbackViewResolver")
    private ViewResolver fallbackViewResolver;

    @Autowired
    @Qualifier("beanNameViewResolver")
    private ViewResolver beanNameViewResolver;

    @Autowired
    @Qualifier("overrideViewResolver")
    private ViewResolver overrideViewResolver;


    @Test
    public void shouldFindOverriddenView() throws Exception {
        //when
        View view = overrideDeviceContextualViewResolver.resolveViewName("Override", Locale.getDefault());

        //then
        assertNotNull(view);
        assertTrue(view.toString().contains("Override.android.jsp"));
    }

    @Test
    public void shouldDetectDeviceFamilyAndChangeViewName() throws Exception {
        //when
        View view = deviceContextualViewResolver.resolveViewName("TestView", Locale.getDefault());

        //then
        assertNotNull(view);
        assertTrue(view.toString().contains("TestView.android.jsp"));
    }

    @Test
    public void shouldDetectRedirectOrForwardRequests() throws Exception {
        //given 

        //when
        View view = forwardRedirectViewResolver.resolveViewName("redirect:RedirectTestView", Locale.getDefault());
        View view2 = forwardRedirectViewResolver.resolveViewName("forward:ForwardTestView", Locale.getDefault());

        //then
        assertTrue(view instanceof RedirectView);
        assertTrue(view2 instanceof InternalResourceView);
        assertEquals("RedirectTestView", ((RedirectView) view).getUrl());
        assertEquals("ForwardTestView", ((InternalResourceView) view2).getUrl());
    }

    @Test
    public void shouldFindNormalView() throws Exception {
        //given 

        //when
        View view = fallbackViewResolver.resolveViewName("TestView", Locale.getDefault());

        //then
        assertNotNull(view);
        assertTrue(view.toString().contains("TestView.jsp"));
    }

    @Test
    public void shouldFindOverridenView() throws Exception {
        //given

        //when
        View view = overrideViewResolver.resolveViewName("TestView2", Locale.getDefault());

        //then
        assertNotNull(view);
        assertTrue(view.toString().contains("TestView2.jsp"));
    }

    @Test
    public void shouldInitializeResolverInCorrectOrder() {
        //given
        Ordered v1 = (Ordered) forwardRedirectViewResolver;
        Ordered v2 = (Ordered) overrideDeviceContextualViewResolver;
        Ordered v3 = (Ordered) deviceContextualViewResolver;
        Ordered v4 = (Ordered) overrideViewResolver;
        Ordered v5 = (Ordered) beanNameViewResolver;
        Ordered v6 = (Ordered) fallbackViewResolver;

        //when
        //then
        //highest number is lowest precedence
        assertTrue(v6.getOrder() > v5.getOrder());
        assertTrue(v5.getOrder() > v4.getOrder());
        assertTrue(v4.getOrder() > v3.getOrder());
        assertTrue(v3.getOrder() > v2.getOrder());
        assertTrue(v2.getOrder() > v1.getOrder());
    }


    @Test
    public void shouldSerializeDatesUsingISO_8601() throws IOException {
        //given
        ObjectMapper objectMapper = new DxaSpringInitialization().objectMapper();
        long timestamp = 42000;
        String example = "{\"Date\" : \"1970-01-01T00:00:42.000+0000\", " +
                "\"DateZ\" : \"1970-01-01T00:00:42.000Z\"," +
                "\"DateTime\" : \"1970-01-01T00:00:42.000Z\"," +
                "\"DateTimeFull\" : \"1970-01-01T00:00:42.000+0000\"}";

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

        @Bean("overrideDeviceContextualViewResolver")
        public ViewResolver overrideDeviceContextualViewResolver() {
            return dxaSpringInitialization().overrideDeviceContextualViewResolver();
        }

        @Bean("fallbackViewResolver")
        public ViewResolver fallbackViewResolver() {
            return dxaSpringInitialization().fallbackViewResolver();
        }

        @Bean("forwardRedirectViewResolver")
        public ViewResolver forwardRedirectViewResolver() {
            return dxaSpringInitialization().forwardRedirectViewResolver();
        }

        @Bean("deviceContextualViewResolver")
        public ViewResolver deviceContextualViewResolver() {
            return dxaSpringInitialization().deviceContextualViewResolver();
        }

        @Bean("beanNameViewResolver")
        public ViewResolver beanNameViewResolver() {
            return dxaSpringInitialization().beanNameViewResolver();
        }

        @Bean("overrideViewResolver")
        public ViewResolver overrideViewResolver() {
            return dxaSpringInitialization().overrideViewResolver();
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
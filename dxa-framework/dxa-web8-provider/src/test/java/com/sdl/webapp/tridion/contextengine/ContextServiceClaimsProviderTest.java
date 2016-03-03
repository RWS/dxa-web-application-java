package com.sdl.webapp.tridion.contextengine;

import com.sdl.context.odata.client.api.ODataContextEngine;
import com.sdl.webapp.common.exceptions.DxaException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.apache.commons.collections.MapUtils.isEmpty;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class ContextServiceClaimsProviderTest {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ContextServiceClaimsProvider contextServiceClaimsProvider;

    @Before
    public void init() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));
    }

    @Test
    public void shouldReturnEmptyMapIfContextEngineGaveNull() throws DxaException {
        //given

        //when
        Map<String, Object> contextClaims = contextServiceClaimsProvider.getContextClaims(null);

        //then
        assertNotNull(contextClaims);
        assertTrue(isEmpty(contextClaims));
    }

    @Configuration
    @Profile("test")
    public static class SpringConfigurationContext {
        @Bean
        public HttpServletRequest httpServletRequest() {
            return new MockHttpServletRequest();
        }

        @Bean
        public ContextServiceClaimsProvider contextServiceClaimsProvider() {
            return new ContextServiceClaimsProvider();
        }

        @Bean
        public ODataContextEngine oDataContextEngine() {
            return mock(ODataContextEngine.class);
        }
    }
}
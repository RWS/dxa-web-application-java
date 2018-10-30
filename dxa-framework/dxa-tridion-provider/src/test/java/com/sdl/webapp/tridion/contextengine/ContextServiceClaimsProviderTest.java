package com.sdl.webapp.tridion.contextengine;

import com.sdl.context.api.exception.ResolverException;
import com.sdl.context.api.resolution.Evidence;
import com.sdl.context.odata.client.api.ODataContextEngine;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.exceptions.DxaException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.apache.commons.collections4.MapUtils.isEmpty;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextServiceClaimsProviderTest {

    @Mock
    private HttpServletRequest httpServletRequest = new MockHttpServletRequest();

    @Mock
    private ODataContextEngine oDataContextEngine;

    @Mock
    private WebRequestContext webRequestContext;

    @Mock
    private Localization localization;

    @InjectMocks
    private ContextServiceClaimsProvider contextServiceClaimsProvider;

    @Before
    public void init() {
        when(webRequestContext.getLocalization()).thenReturn(localization);

        when(localization.getId()).thenReturn("1");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));
        ReflectionTestUtils.setField(contextServiceClaimsProvider, "oDataContextEngine", oDataContextEngine);
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

    @Test
    public void shouldPassLocalizationIdToContextService() throws DxaException, ResolverException {
        //given
        ReflectionTestUtils.setField(contextServiceClaimsProvider, "isPublicationIdExpected", true);

        //when
        contextServiceClaimsProvider.getContextClaims(null);

        //then
        verify(localization).getId();
        verify(oDataContextEngine).resolve(argThat(new BaseMatcher<Evidence>() {
            @Override
            public boolean matches(Object item) {
                return ((Evidence) item).get("publication-id").getValue().equals(1);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Evidence should contain publication ID");
            }
        }));
    }

    @Test
    public void shouldNotPassLocalizationIdToContextServiceByDefault() throws DxaException, ResolverException {
        //given
        // actually default, but here to show it explicitly for better reading
        ReflectionTestUtils.setField(contextServiceClaimsProvider, "isPublicationIdExpected", false);

        //when
        contextServiceClaimsProvider.getContextClaims(null);

        //then
        verify(oDataContextEngine).resolve(argThat(new BaseMatcher<Evidence>() {
            @Override
            public boolean matches(Object item) {
                return ((Evidence) item).get("publication-id") == null;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Evidence should not contain publication ID");
            }
        }));
    }
}
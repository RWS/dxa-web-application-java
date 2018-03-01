package com.sdl.webapp.common.impl.interceptor;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;

public class HealthCheckFilterTest {

    @Test
    public void shouldReturn200() throws Exception {
        //given 
        HealthCheckFilter filter = new HealthCheckFilter();
        MockHttpServletResponse response = new MockHttpServletResponse();

        //when
        filter.doFilter(null, response, null);

        //then
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }
}
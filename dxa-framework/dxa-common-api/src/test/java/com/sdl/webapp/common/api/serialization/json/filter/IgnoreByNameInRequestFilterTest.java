package com.sdl.webapp.common.api.serialization.json.filter;

import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.google.common.base.Splitter;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IgnoreByNameInRequestFilterTest {

    private HttpServletRequest httpServletRequest = new MockHttpServletRequest();

    private IgnoreByNameInRequestFilter filter = new IgnoreByNameInRequestFilter(httpServletRequest);

    private static final String REQUEST_FILTER = "Ignore_By_Name_In_Request_Filter";

    @Test
    public void shouldSetPropertiesToIgnore() {
        //given 
        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        IgnoreByNameInRequestFilter.ignoreByName(request, "prop1", "prop2");

        //then
        List<String> list = Splitter.on(",").splitToList(request.getAttribute(REQUEST_FILTER).toString());
        assertTrue(list.contains("prop1"));
        assertTrue(list.contains("prop2"));
        assertTrue(list.size() == 2);
    }

    @Test
    public void shouldNotAddAttributeIfPropertiesNull() {
        //given 
        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        IgnoreByNameInRequestFilter.ignoreByName(request);
        IgnoreByNameInRequestFilter.ignoreByName(null);

        //then
        assertNull(request.getAttribute(REQUEST_FILTER));
    }

    @Test
    public void shouldIncludeIfRequestIsNull() {
        //when
        boolean include = new IgnoreByNameInRequestFilter(null).include(mock(PropertyWriter.class));

        //then
        assertTrue(include);
    }

    @Test
    public void shouldIncludeIfAttributeNotSet() {
        //when
        boolean include = filter.include(mock(PropertyWriter.class));

        //then
        assertTrue(include);
    }

    @Test
    public void shouldExcludePropertiesThatAreSet() {
        //given 
        PropertyWriter writer = mock(PropertyWriter.class);
        lenient().when(writer.getName()).thenReturn("test2");
        IgnoreByNameInRequestFilter.ignoreByName(httpServletRequest, "test1", "test2");

        //when
        boolean include = filter.include(writer);

        //then
        assertFalse(include);
    }

    @Test
    public void shouldNotExcludePropertiesThatAreSetButWriterIsDifferent() {
        //given
        PropertyWriter writer = mock(PropertyWriter.class);
        lenient().when(writer.getName()).thenReturn("test3");
        IgnoreByNameInRequestFilter.ignoreByName(httpServletRequest, "test1", "test2");

        //when
        boolean include = filter.include(writer);

        //then
        assertTrue(include);
    }
}
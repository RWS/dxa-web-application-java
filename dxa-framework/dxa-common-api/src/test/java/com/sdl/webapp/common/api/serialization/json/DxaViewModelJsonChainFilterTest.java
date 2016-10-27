package com.sdl.webapp.common.api.serialization.json;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.std.MapProperty;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DxaViewModelJsonChainFilterTest {

    @Test
    public void shouldIncludeIfFiltersNotSet() {
        //when
        boolean isTrue = new DxaViewModelJsonChainFilter().include(new MapProperty(null));

        //then
        assertTrue(isTrue);
    }

    @Test
    public void shouldExcludeIfAnyFilterIsExcluding() {
        //given 
        PropertyWriter writer = mock(PropertyWriter.class);
        DxaViewModelJsonPropertyFilter filter1 = mock(DxaViewModelJsonPropertyFilter.class);
        when(filter1.include(writer)).thenReturn(true);

        DxaViewModelJsonPropertyFilter filter2 = mock(DxaViewModelJsonPropertyFilter.class);
        when(filter2.include(writer)).thenReturn(false);

        DxaViewModelJsonChainFilter filter = new DxaViewModelJsonChainFilter();
        setFilters(filter, newArrayList(filter1, filter2));


        //when
        boolean isFalse = filter.include(writer);

        //then
        assertFalse(isFalse);
    }

    @Test
    public void shouldIncludeIfAllFiltersInclude() {
        //given
        PropertyWriter writer = mock(PropertyWriter.class);
        DxaViewModelJsonPropertyFilter filter1 = mock(DxaViewModelJsonPropertyFilter.class);
        when(filter1.include(writer)).thenReturn(true);

        DxaViewModelJsonPropertyFilter filter2 = mock(DxaViewModelJsonPropertyFilter.class);
        when(filter2.include(writer)).thenReturn(true);

        DxaViewModelJsonChainFilter filter = new DxaViewModelJsonChainFilter();
        setFilters(filter, newArrayList(filter1, filter2));


        //when
        boolean isTrue = filter.include(writer);

        //then
        assertTrue(isTrue);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldIncludeBeanPropertyWriter() {
        //given 

        //when
        boolean include = new DxaViewModelJsonChainFilter().include(mock(BeanPropertyWriter.class));

        //then
        assertTrue(include);
    }

    private void setFilters(DxaViewModelJsonChainFilter filter, List<DxaViewModelJsonPropertyFilter> list) {
        ReflectionTestUtils.setField(filter, "filters", list);
    }
}
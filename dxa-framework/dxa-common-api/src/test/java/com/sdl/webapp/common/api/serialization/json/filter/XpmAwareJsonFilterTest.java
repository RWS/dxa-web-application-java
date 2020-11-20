package com.sdl.webapp.common.api.serialization.json.filter;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.serialization.json.annotation.JsonXpmAware;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XpmAwareJsonFilterTest {

    @Mock
    private WebRequestContext webRequestContext;

    @InjectMocks
    private XpmAwareJsonFilter xpmAwareJsonFilter;

    @Before
    public void init() {
        ReflectionTestUtils.setField(xpmAwareJsonFilter, "enabled", true);
    }

    
    @Test
    public void shouldIncludeIfNoWebRequestContext() {
        //given
        AnnotatedMember member = getAnnotatedMember(JsonXpmAware.class);
        PropertyWriter writer = propertyWriter(BeanPropertyWriter.class, "Test", member);

        XpmAwareJsonFilter filter = new XpmAwareJsonFilter(null);

        //when
        boolean include = filter.include(writer);

        //then
        assertTrue(include);
    }

    @Test
    public void shouldExcludeIfNotXpmAndAnnotated() {
        //given
        AnnotatedMember member = getAnnotatedMember(JsonXpmAware.class);
        PropertyWriter writer = propertyWriter(BeanPropertyWriter.class, "Test", member);

        when(webRequestContext.isPreview()).thenReturn(false);

        //when
        boolean include = xpmAwareJsonFilter.include(writer);

        //then
        assertFalse(include);
    }

    @Test
    public void shouldIncludeIfNotAnnotated() {
        //given
        AnnotatedMember member = getAnnotatedMember(null);
        PropertyWriter writer = propertyWriter(BeanPropertyWriter.class, "Test", member);

        //when
        boolean include = xpmAwareJsonFilter.include(writer);

        //then
        assertTrue(include);
    }

    @Test
    public void shouldFallBackToNamesIfNotBeanPropertyWriter() {
        //given
        PropertyWriter writer1 = propertyWriter(PropertyWriter.class, "XpmMetadata", null);
        PropertyWriter writer2 = propertyWriter(PropertyWriter.class, "XpmPropertyMetadata", null);

        when(webRequestContext.isPreview()).thenReturn(true);

        //when
        boolean include = xpmAwareJsonFilter.include(writer1);
        boolean include1 = xpmAwareJsonFilter.include(writer2);

        //then
        assertTrue(include);
        assertTrue(include1);
    }

    @Test
    public void shouldIncludeIfNotEnabled() {
        //given 
        XpmAwareJsonFilter filter = new XpmAwareJsonFilter(null);

        //when
        boolean include = filter.include(null);

        //then
        assertTrue(include);
    }

    private AnnotatedMember getAnnotatedMember(Class<? extends Annotation> annotation) {
        AnnotationMap annMap = new AnnotationMap();
        if (annotation != null) {
            ReflectionTestUtils.setField(annMap, "_annotations", new HashMap<Class<?>, Annotation>() {
                {
                    put(annotation, mock(annotation));
                }
            });
        }
        return new AnnotatedField(null, null, annMap);
    }

    private PropertyWriter propertyWriter(Class<? extends PropertyWriter> aClass, String propName, AnnotatedMember member) {
        PropertyWriter writer = mock(aClass);
        when(writer.getFullName()).thenReturn(mock(PropertyName.class));
        if (aClass.equals(BeanPropertyWriter.class)) {
            when(writer.getMember()).thenReturn(member);
        }
        return writer;
    }

}
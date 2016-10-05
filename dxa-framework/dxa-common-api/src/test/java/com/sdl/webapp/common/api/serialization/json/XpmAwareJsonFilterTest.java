package com.sdl.webapp.common.api.serialization.json;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.serialization.json.annotation.JsonXpmAware;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.annotation.Annotation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XpmAwareJsonFilterTest {

    @Mock
    private WebRequestContext webRequestContext;

    @InjectMocks
    private XpmAwareJsonFilter xpmAwareJsonFilter;

    @Test
    public void shouldIncludeIfXpmAndAnnotated() {
        //given
        AnnotatedMember member = getAnnotatedMember(mock(JsonXpmAware.class));
        PropertyWriter writer = propertyWriter(BeanPropertyWriter.class, "Test", member);

        when(webRequestContext.isPreview()).thenReturn(true);

        //when
        boolean include = xpmAwareJsonFilter.include(writer);

        //then
        assertTrue(include);
    }

    @Test
    public void shouldIncludeIfNoWebRequestContext() {
        //given
        AnnotatedMember member = getAnnotatedMember(mock(JsonXpmAware.class));
        PropertyWriter writer = propertyWriter(BeanPropertyWriter.class, "Test", member);

        when(webRequestContext.isPreview()).thenReturn(true);

        XpmAwareJsonFilter filter = new XpmAwareJsonFilter(null);

        //when
        boolean include = filter.include(writer);

        //then
        assertTrue(include);
    }

    @Test
    public void shouldExcludeIfNotXpmAndAnnotated() {
        //given
        AnnotatedMember member = getAnnotatedMember(mock(JsonXpmAware.class));
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

    private AnnotatedMember getAnnotatedMember(Annotation annotation) {
        AnnotatedMember member = mock(AnnotatedMember.class);
        doReturn(annotation).when(member).getAnnotation(eq(JsonXpmAware.class));
        return member;
    }

    private PropertyWriter propertyWriter(Class<? extends PropertyWriter> aClass, String propName, AnnotatedMember member) {
        PropertyWriter writer = mock(aClass);
        when(writer.getName()).thenReturn(propName);
        when(writer.getFullName()).thenReturn(mock(PropertyName.class));
        if (aClass.equals(BeanPropertyWriter.class)) {
            when(((BeanPropertyWriter) writer).getMember()).thenReturn(member);
        }
        return writer;
    }

}
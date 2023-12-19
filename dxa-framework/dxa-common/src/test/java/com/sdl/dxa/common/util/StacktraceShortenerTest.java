package com.sdl.dxa.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class StacktraceShortenerTest {
    private final StackTraceElement[] EMPTY = new StackTraceElement[0];
    private StacktraceShortener shortener;

    private StackTraceElement[] trace3 = new StackTraceElement[] {
            new StackTraceElement("org.apache.coyote.AbstractProcessorLight", "process", "AbstractProcessorLight.java", 66),
            new StackTraceElement("org.apache.tomcat.util.net.AbstractEndpoint", "processSocket", "AbstractEndpoint.java", 1050),
            new StackTraceElement("org.apache.catalina.connector.CoyoteAdapter", "service", "CoyoteAdapter.java", 342),
    };

    private StackTraceElement[] trace2 = new StackTraceElement[] {
            new StackTraceElement("com.sdl.dxa.modelservice.service.ContentService", "loadPageContent", "ContentService.java", 175),
            new StackTraceElement("sun.reflect.GeneratedMethodAccessor341", "invoke", null, 0),
            new StackTraceElement("sun.reflect.DelegatingMethodAccessorImpl", "invoke", null, 0),
            new StackTraceElement("java.lang.reflect.Method", "invoke", null, 0),
            new StackTraceElement("org.springframework.aop.support.AopUtils", "invokeJoinpointUsingReflection", "AopUtils.java", 333),
            new StackTraceElement("org.springframework.aop.framework.ReflectiveMethodInvocation", "invokeJoinpoint", "ReflectiveMethodInvocation.java", 190),
            new StackTraceElement("org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation", "invokeJoinpoint", "CglibAopProxy.java", 723),
            new StackTraceElement("org.springframework.aop.framework.ReflectiveMethodInvocation", "proceed", "ReflectiveMethodInvocation.java", 157),
            new StackTraceElement("org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor", "intercept", "CglibAopProxy.java", 655),
            new StackTraceElement("com.sdl.dxa.modelservice.service.ContentService$$EnhancerBySpringCGLIB$$4ce1056c", "loadPageContent", null, 0),
            new StackTraceElement("com.sdl.dxa.modelservice.service.DefaultPageModelService", "_expandIncludePages", "DefaultPageModelService.java", 210),
            new StackTraceElement("com.sdl.dxa.modelservice.service.DefaultPageModelService", "_processR2PageModel", "DefaultPageModelService.java", 172),
            new StackTraceElement("com.sdl.dxa.modelservice.service.DefaultPageModelService", "loadPageModel", "DefaultPageModelService.java", 108),
            new StackTraceElement("com.sdl.dxa.modelservice.controller.PageModelController", "getPage", "PageModelController.java", 103),
            new StackTraceElement("sun.reflect.GeneratedMethodAccessor123", "invoke", null, 0),
            new StackTraceElement("sun.reflect.DelegatingMethodAccessorImpl", "invoke", null, 0),
            new StackTraceElement("sun.reflect.Method", "invoke", null, 0),
            new StackTraceElement("org.springframework.web.method.support.InvocableHandlerMethod", "doInvoke", "InvocableHandlerMethod.java", 221),
            new StackTraceElement("org.springframework.web.method.support.InvocableHandlerMethod", "invokeForRequest", "InvocableHandlerMethod.java", 135),
            new StackTraceElement("org.apache.coyote.AbstractProcessorLight", "process", "AbstractProcessorLight.java", 66),
            new StackTraceElement("org.apache.tomcat.util.net.AbstractEndpoint", "processSocket", "AbstractEndpoint.java", 1050),
            new StackTraceElement("org.apache.catalina.connector.CoyoteAdapter", "service", "CoyoteAdapter.java", 342),
    };

    private StackTraceElement[] trace1;

    @BeforeEach
    public void setUp() {
        List<StackTraceElement> list = new ArrayList<>(Arrays.asList(trace2));
        list.add(new StackTraceElement("java.lang.Thread", "run", null, 0));
        trace1 = list.toArray(EMPTY);
    }

    @Test
    public void testFullStacktraceStartedNotFromCompactingThing() {
        Exception cause = new Exception();
        cause.setStackTrace(trace1);
        shortener = new StacktraceShortener(cause);
        String shortenedStacktrace = shortener.generateString();

        assertTrue(shortenedStacktrace.contains("com.sdl.dxa.modelservice."));
        assertFalse(shortenedStacktrace.contains("org.springframework"));

        assertTrue(shortenedStacktrace.contains("SPRING"));
        assertTrue(shortenedStacktrace.contains("REFLECTION"));
        assertTrue(shortenedStacktrace.contains("TOMCAT"));
    }

    @Test
    public void testFullStacktraceStartedFromCompactingThing() {
        Exception cause = new Exception();
        cause.setStackTrace(trace2);
        shortener = new StacktraceShortener(cause);
        shortener.addRuleToBeLeftExpanded("SDL", new String[] {"com.sdl.", "org.dd4t."});
        String shortenedStacktrace = shortener.generateString();

        assertTrue(shortenedStacktrace.contains("TOMCAT"));
    }

    @Test
    public void testFullStacktraceOnlyCompactingThing() {
        Exception cause = new Exception();
        cause.setStackTrace(trace3);
        shortener = new StacktraceShortener(cause);
        String shortenedStacktrace = shortener.generateString();

        assertTrue(shortenedStacktrace.contains("TOMCAT"));
    }

    @Test
    public void testExc() {
        try{
            IllegalStateException ise = new IllegalStateException("keep off this");
            throw new IllegalArgumentException("Note", ise);
        } catch (Exception ex) {
            shortener = new StacktraceShortener(ex);
            ex.printStackTrace(System.err);
            System.err.println("=========");
            System.err.println(shortener.generateString());
        }
    }
}

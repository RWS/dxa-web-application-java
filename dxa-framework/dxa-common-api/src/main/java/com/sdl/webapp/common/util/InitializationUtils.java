package com.sdl.webapp.common.util;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventListener;
import java.util.List;
import java.util.Properties;

@Slf4j
public final class InitializationUtils {

    // keep the order for internal usage
    private static List<Resource> resources;
    private static Properties properties;

    private InitializationUtils() {
    }

    //todo dxa2 replace with aspects
    public static void traceBeanInitialization(Object bean) {
        log.trace("Bean initialization: {}", bean);
    }

    /**
     * <p>Loads all properties from classpath from two files <code>dxa.defaults.properties</code>
     * and <code>dxa.properties</code>. The latter has a priority is exists.</p>
     * <p>If your module has its own properties, you should load them manually when using this method.
     * Despite DXA loads all dxa.**.properties into a Spring context, this method does not.</p>
     * <p>Once loaded properties are cached permanently.</p>
     *
     * @return merged properties for DXA
     */
    @SneakyThrows(IOException.class)
    public static Properties loadDxaProperties() {
        if (properties == null) {
            Properties dxaProperties = new Properties();
            for (Resource resource : getAllResources()) {
                // order is guaranteed by #getAllResources() and internal usage of List
                PropertiesLoaderUtils.fillProperties(dxaProperties, resource);
                log.debug("Properties from {} are loaded", resource);
            }

            properties = dxaProperties;
        }

        log.debug("Properties {} returned", properties);
        return properties;
    }

    /**
     * Loads all available DXA properties files.
     * <p>
     * <p>Respects <code>dxa.defaults.properties</code>,
     * <code>classpath*:/dxa.modules.*.properties</code>, <code>dxa.properties</code>,
     * <code>classpath*:/dxa.addons.*.properties</code>.</p>
     *
     * @return a collection of DXA properties files
     */
    @SneakyThrows(IOException.class)
    public static Collection<Resource> getAllResources() {
        if (resources == null) {
            PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            patternResolver.setPathMatcher(new AntPathMatcher());

            List<Resource> availableResources = new ArrayList<>();
            //note that the order of properties is important because of overriding of properties
            availableResources.add(new ClassPathResource("dxa.defaults.properties"));
            availableResources.addAll(Arrays.asList(patternResolver.getResources("classpath*:/dxa.modules.*.properties")));
            availableResources.add(new ClassPathResource("dxa.properties"));
            availableResources.addAll(Arrays.asList(patternResolver.getResources("classpath*:/dxa.addons.*.properties")));

            log.debug("Loaded resources {}", availableResources);
            resources = availableResources;
        }

        log.debug("Returned list of resources {}", resources);
        return resources;
    }

    /**
     * <p>Registers servlet in the given servlet context.</p>
     *
     * @param servletContext current servlet context
     * @param servlet        servlet to register
     * @param mapping        mapping for the servlet
     * @return dynamic registration object
     */
    public static ServletRegistration.Dynamic registerServlet(@NonNull ServletContext servletContext,
                                                              @NonNull Servlet servlet, @NonNull String mapping) {
        ServletRegistration.Dynamic registration = servletContext.addServlet(servlet.getClass().getName(), servlet);
        registration.addMapping(mapping);
        log.debug("Registered {} for mapping {}", servlet.getClass(), mapping);
        return registration;
    }

    /**
     * <p>Registers servlet in the given servlet context if class is found in classpath.</p>
     *
     * @param servletContext current servlet context
     * @param className      classname of a servlet to register
     * @param mapping        mapping for the servlet
     * @return dynamic registration object or <code>null</code> if classname if not found
     */
    public static ServletRegistration.Dynamic registerServlet(@NonNull ServletContext servletContext,
                                                              @NonNull String className, @NonNull String mapping) {
        if (isClassPresent(className)) {
            ServletRegistration.Dynamic registration = servletContext.addServlet(className, className);
            registration.addMapping(mapping);
            log.debug("Registered {} for mapping {}", className, mapping);
            return registration;
        }
        log.debug("Failed to register {}", className);
        return null;
    }

    /**
     * <p>Says whether the given class is in classpath.</p>
     *
     * @param className classname of a filter to search
     * @return true if class is in classpath, false otherwise
     */
    public static boolean isClassPresent(@NonNull String className) {
        return ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
    }

    /**
     * Returns the given class if present in classpath.
     *
     * @param className classname to search
     * @return class if present in classpath, null otherwise
     */
    @SneakyThrows({ClassNotFoundException.class, LinkageError.class})
    public static Class<?> classForNameIfPresent(@NonNull String className) {
        return isClassPresent(className) ? ClassUtils.forName(className, ClassUtils.getDefaultClassLoader()) : null;
    }

    /**
     * <p>Registers filter in the given servlet context.</p>
     *
     * @param servletContext current servlet context
     * @param clazz          classname of a filter to register
     * @param urlMappings    mappings for the filter
     * @return dynamic registration object or <code>null</code> if classname if not found
     */
    public static FilterRegistration.Dynamic registerFilter(@NonNull ServletContext servletContext,
                                                            @NonNull Class<? extends Filter> clazz, @NonNull String... urlMappings) {

        FilterRegistration.Dynamic registration = servletContext.addFilter(clazz.getName(), clazz);
        registration.addMappingForUrlPatterns(null, false, urlMappings);
        log.debug("Registered {} for mapping {}", clazz.getName(), urlMappings);
        return registration;
    }

    /**
     * <p>Registers filter in the given servlet context if class is found in classpath.</p>
     *
     * @param servletContext current servlet context
     * @param className      classname of a filter to register
     * @param urlMappings    mappings for the filter
     * @return dynamic registration object or <code>null</code> if classname if not found
     */
    public static FilterRegistration.Dynamic registerFilter(@NonNull ServletContext servletContext,
                                                            @NonNull String className, @NonNull String... urlMappings) {
        if (isClassPresent(className)) {
            FilterRegistration.Dynamic registration = servletContext.addFilter(className, className);
            registration.addMappingForUrlPatterns(null, false, urlMappings);
            log.debug("Registered {} for mapping {}", className, urlMappings);
            return registration;
        }
        log.debug("Failed to register {}", className);
        return null;
    }

    /**
     * <p>Registers filter in the given servlet context.</p>
     *
     * @param servletContext current servlet context
     * @param filter         filter to register
     * @param urlMappings    mappings for the filter
     * @return dynamic registration object
     */
    public static FilterRegistration.Dynamic registerFilter(@NonNull ServletContext servletContext,
                                                            @NonNull Filter filter, @NonNull String... urlMappings) {
        FilterRegistration.Dynamic registration = servletContext.addFilter(filter.getClass().getName(), filter);
        registration.addMappingForUrlPatterns(null, false, urlMappings);
        log.debug("Registered {} for mapping {}", filter.getClass().getName(), urlMappings);
        return registration;
    }

    /**
     * <p>Registers event listener in the given servlet context.</p>
     *
     * @param servletContext current servlet context
     * @param className      classname of a listener to register
     */
    public static void registerListener(@NonNull ServletContext servletContext, @NonNull String className) {
        if (isClassPresent(className)) {
            servletContext.addListener(className);
            log.debug("Registered {}", className);
        } else {
            log.debug("Failed to register {}", className);
        }
    }

    /**
     * <p>Registers event listener in the given servlet context.</p>
     *
     * @param servletContext current servlet context
     * @param listenerClass  class of a listener to register
     */
    public static void registerListener(@NonNull ServletContext servletContext,
                                        @NonNull Class<? extends EventListener> listenerClass) {
        servletContext.addListener(listenerClass);
        log.debug("Registered {}", listenerClass.getClass().getName());
    }

    /**
     * <p>Registers event listener in the given servlet context.</p>
     *
     * @param servletContext current servlet context
     * @param listener       class of a listener to register
     */
    public static void registerListener(@NonNull ServletContext servletContext, @NonNull EventListener listener) {
        servletContext.addListener(listener);
        log.debug("Registered {}", listener.getClass().getName());
    }
}

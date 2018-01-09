package com.sdl.webapp.common.util;

import com.google.common.base.Joiner;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;

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

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Initialization utils to deal with properties and other configuration and startup elements.
 *
 * @dxa.publicApi
 */
@Slf4j
public final class InitializationUtils {

    private static final String REGISTERED_FOR_MAPPING_LOG_MESSAGE = "Registered {} for mapping {}";

    private static final String REGISTERED_LOG_MESSAGE = "Registered {}";

    private static final String SPRING_PROFILES_INCLUDE = "spring.profiles.include";

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
     * Loads all properties from classpath from known files in the order defined by {@link #getAllResources()}.
     * <p>Once loaded properties are cached permanently.</p>
     * <p>There is a special treatment for {@code spring.profiles.include} property: it's collected from all properties files appending latter values.</p>
     *
     * @return merged properties for DXA
     */
    @SneakyThrows(IOException.class)
    public static Properties loadDxaProperties() {
        if (properties == null) {
            Properties dxaProperties = new Properties();
            for (Resource resource : getAllResources()) {
                Properties current = new Properties();
                // order is guaranteed by #getAllResources() and internal usage of List
                current.load(new BOMInputStream(resource.getInputStream()));
                log.debug("Properties from {} are loaded", resource);

                if (current.containsKey(SPRING_PROFILES_INCLUDE) && dxaProperties.containsKey(SPRING_PROFILES_INCLUDE)) {
                    current.setProperty(SPRING_PROFILES_INCLUDE,
                            Joiner.on(',').join(dxaProperties.getProperty(SPRING_PROFILES_INCLUDE), current.getProperty(SPRING_PROFILES_INCLUDE)));
                }

                dxaProperties.putAll(current);
            }

            properties = dxaProperties;
        }

        log.trace("Properties {} returned", properties);
        return properties;
    }

    /**
     * Loads all available DXA properties files. Respects following files in following order:
     * <pre>
     *  <code>dxa.defaults.properties</code>,
     *  <code>classpath*:/dxa.modules.*.properties</code>,
     *  <code>dxa.properties</code>,
     *  <code>classpath*:/dxa.addons.*.properties</code></pre>
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
            ClassPathResource dxaProperties = new ClassPathResource("dxa.properties");
            if (dxaProperties.exists()) {
                availableResources.add(dxaProperties);
            }
            availableResources.addAll(Arrays.asList(patternResolver.getResources("classpath*:/dxa.addons.*.properties")));

            log.debug("Loaded resources {}", availableResources);
            resources = availableResources;
        }

        log.trace("Returned list of resources {}", resources);
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
        log.info(REGISTERED_FOR_MAPPING_LOG_MESSAGE, servlet.getClass(), mapping);
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
            log.debug(REGISTERED_FOR_MAPPING_LOG_MESSAGE, className, mapping);
            return registration;
        }
        log.info("Failed to register {}", className);
        return null;
    }

    /**
     * <p>Says whether the given class is in classpath.</p>
     *
     * @param className classname of a filter to search
     * @return true if class is in classpath, false otherwise
     */
    public static boolean isClassPresent(@NonNull String className) {
        boolean present = ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
        log.trace("{} isClassPresent: {}", className, present);
        return present;
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
     * Loads <code>spring.profiles.active</code> and <code>spring.profiles.include</code> Spring profiles merging them.
     * In case <code>spring.profiles.active</code> is found in current context (e.g. from web.xml),
     * then this has a priority and properties are skipped.
     *
     * @param applicationContext current configurable application context
     */
    public static void loadActiveSpringProfiles(ServletContext servletContext, ConfigurableWebApplicationContext applicationContext) {
        if (servletContext.getInitParameter("spring.profiles.active") != null) {
            log.debug("spring.profiles.active property is found in current servlet context which has a priority on properties definition");
            return;
        }

        Properties dxaProperties = loadDxaProperties();

        addActiveProfiles(applicationContext, dxaProperties.getProperty("spring.profiles.active"));
        addActiveProfiles(applicationContext, dxaProperties.getProperty("spring.profiles.include"));
    }

    private static void addActiveProfiles(ConfigurableWebApplicationContext servletAppContext, String activeProfiles) {
        if (!isEmpty(activeProfiles)) {
            ConfigurableEnvironment environment = servletAppContext.getEnvironment();
            Arrays.stream(activeProfiles.split(","))
                    .filter(StringUtils::isNotEmpty)
                    .forEach(profile -> environment.addActiveProfile(profile.trim()));
        }
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
        log.info(REGISTERED_FOR_MAPPING_LOG_MESSAGE, clazz.getName(), urlMappings);
        return registration;
    }

    /**
     * Registers filter in the given servlet context with a given name.
     * <p>Typically name of the filter is a classname as implemented in {@link #registerFilter(ServletContext, Class, String...)} and
     * {@link #registerFilter(ServletContext, Filter, String...)} and {@link #registerFilter(ServletContext, String, String...)}.</p>
     * <p>Use this name if filter name is important for you implementation. Otherwise there will be no guarantee that same filter
     * is not registered twice from different parts of code since filter registration is aware of filter name which is aligned for those
     * three methods</p>
     *
     * @param servletContext current servlet context
     * @param filterName     name of the filter to register
     * @param clazz          classname of a filter to register
     * @param urlMappings    mappings for the filter
     * @return dynamic registration object or <code>null</code> if classname if not found
     */
    public static FilterRegistration.Dynamic registerFilter(@NonNull ServletContext servletContext, @NonNull String filterName,
                                                            @NonNull Class<? extends Filter> clazz, @NonNull String... urlMappings) {

        FilterRegistration.Dynamic registration = servletContext.addFilter(filterName, clazz);
        registration.addMappingForUrlPatterns(null, false, urlMappings);
        log.info(REGISTERED_FOR_MAPPING_LOG_MESSAGE, filterName, urlMappings);
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
            log.info(REGISTERED_FOR_MAPPING_LOG_MESSAGE, className, urlMappings);
            return registration;
        }
        log.warn("Failed to register {}, class is not in classpath", className);
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
        log.info(REGISTERED_FOR_MAPPING_LOG_MESSAGE, filter.getClass().getName(), urlMappings);
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
            log.info(REGISTERED_LOG_MESSAGE, className);
        } else {
            log.warn("Failed to register {}, class is not in classpath", className);
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
        log.info(REGISTERED_LOG_MESSAGE, listenerClass.getName());
    }

    /**
     * <p>Registers event listener in the given servlet context.</p>
     *
     * @param servletContext current servlet context
     * @param listener       class of a listener to register
     */
    public static void registerListener(@NonNull ServletContext servletContext, @NonNull EventListener listener) {
        servletContext.addListener(listener);
        log.info(REGISTERED_LOG_MESSAGE, listener.getClass().getName());
    }

    /**
     * Returns the value from configuration by {@code key} specified or {@code defaultValue} if there is no property with such key.
     * Empty values from configuration are treated as values meaning this method doesn't check if the value is blank.
     *
     * @param key          key to look up
     * @param defaultValue default value if configuration property doesn't exist
     * @return value for key, or defaultValue
     * @since 1.7
     */
    @Nullable
    public static String getConfiguration(String key, String defaultValue) {
        return defaultString(loadDxaProperties().getProperty(key), defaultValue);
    }

}

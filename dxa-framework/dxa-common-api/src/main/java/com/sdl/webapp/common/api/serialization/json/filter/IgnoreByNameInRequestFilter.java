package com.sdl.webapp.common.api.serialization.json.filter;

import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.sdl.webapp.common.api.serialization.json.DxaViewModelJsonPropertyFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import static com.google.common.base.Joiner.on;
import static java.util.Arrays.asList;

/**
 * Checks if current request contains list of properties to ignore set with {@link IgnoreByNameInRequestFilter#ignoreByName(ServletRequest, String...)}.
 * If current serialized property name is exactly the same as specified in attribute, it's skipped.
 * @dxa.publicApi
 */
@Slf4j
@Component
public final class IgnoreByNameInRequestFilter implements DxaViewModelJsonPropertyFilter {

    private static final String REQUEST_ATTRIBUTE = "Ignore_By_Name_In_Request_Filter";

    private static final String DELIMITER = ",";

    private final HttpServletRequest httpServletRequest;

    @Autowired
    public IgnoreByNameInRequestFilter(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * Sets a property to ignore during JSON serialization for {@link com.sdl.webapp.common.api.model.AbstractViewModel}.
     *
     * @param request    current request
     * @param properties properties to ignore
     */
    public static void ignoreByName(ServletRequest request, String... properties) {
        if (properties != null && properties.length != 0) {
            String value = on(DELIMITER).skipNulls().join(
                    on(DELIMITER).join(properties), request.getAttribute(REQUEST_ATTRIBUTE));

            request.setAttribute(REQUEST_ATTRIBUTE, value);
            log.trace("Set ignore properties in current request: {}", value);
        }
    }

    @Override
    public boolean include(PropertyWriter writer) {
        if (httpServletRequest == null) {
            return true;
        }

        Object attribute = httpServletRequest.getAttribute(REQUEST_ATTRIBUTE);
        boolean include = !(attribute != null && asList(attribute.toString().split(DELIMITER)).contains(writer.getName()));
        log.trace("Ignore [{}] property [{}]", include, writer.getName());
        return include;
    }
}

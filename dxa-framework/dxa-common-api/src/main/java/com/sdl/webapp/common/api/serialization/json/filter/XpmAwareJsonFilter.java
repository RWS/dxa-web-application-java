package com.sdl.webapp.common.api.serialization.json.filter;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.serialization.json.DxaViewModelJsonPropertyFilter;
import com.sdl.webapp.common.api.serialization.json.annotation.JsonXpmAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Checks if the annotated member is annotated with {@link JsonXpmAware} or, as a fallback option, is named as
 * {@code XpmMetadata} or {@code XpmPropertyMetadata}. If so, checks if this is XPM enabled environment.
 *
 * @dxa.publicApi
 */
@Component
@Slf4j
public final class XpmAwareJsonFilter implements DxaViewModelJsonPropertyFilter {

    private final WebRequestContext webRequestContext;

    @Value("${dxa.json.xpm.aware}")
    private boolean enabled;

    @Autowired
    public XpmAwareJsonFilter(WebRequestContext webRequestContext) {
        this.webRequestContext = webRequestContext;
    }

    @Override
    public boolean include(PropertyWriter writer) {
        if (!enabled) {
            return true;
        }

        boolean isXpmAware;
        if (writer instanceof BeanPropertyWriter) {
            isXpmAware = writer.getMember().hasAnnotation(JsonXpmAware.class);
            log.trace("Property {} is BeanPropertyWriter", writer.getFullName());
        } else {
            isXpmAware = "XpmMetadata".equals(writer.getName()) || "XpmPropertyMetadata".equals(writer.getName());
            log.trace("Property {} XPM awareness is guessed by name", writer.getFullName());
        }

        log.trace("Property {} XPM aware (annotation set): {}", writer.getFullName(), isXpmAware);

        return !isXpmAware || webRequestContext == null || webRequestContext.isPreview();
    }
}

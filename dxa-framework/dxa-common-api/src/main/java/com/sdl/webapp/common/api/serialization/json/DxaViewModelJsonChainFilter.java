package com.sdl.webapp.common.api.serialization.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Chains filters of class {@link DxaViewModelJsonPropertyFilter}.
 * @dxa.publicApi
 */
public final class DxaViewModelJsonChainFilter extends SimpleBeanPropertyFilter {

    /**
     * Name of the filter in {@link ObjectMapper} configuration.
     */
    public static final String FILTER_NAME = "ViewModelFilter";

    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    @Autowired
    private List<DxaViewModelJsonPropertyFilter> filters;

    @Override
    protected boolean include(BeanPropertyWriter writer) {
        return true;
    }

    @Override
    protected boolean include(final PropertyWriter writer) {
        return filters == null || writer == null || !Iterables.any(filters, new Predicate<DxaViewModelJsonPropertyFilter>() {
            @Override
            public boolean apply(DxaViewModelJsonPropertyFilter input) {
                return !input.include(writer);
            }
        });
    }
}

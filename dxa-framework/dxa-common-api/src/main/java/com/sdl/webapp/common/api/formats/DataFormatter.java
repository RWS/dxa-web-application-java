package com.sdl.webapp.common.api.formats;

import org.springframework.web.servlet.ModelAndView;

/**
 * DataFormatter is to be implemented by the class wiring the different format responses.
 *
 * @dxa.publicApi
 */
@FunctionalInterface
public interface DataFormatter {

    /**
     * Returns the {@link ModelAndView} to render the different formats.
     */
    ModelAndView view(Object model);
}

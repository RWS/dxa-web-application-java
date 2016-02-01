package com.sdl.webapp.common.api.formats;

import org.springframework.web.servlet.ModelAndView;

/**
 * DataFormatter, to be implemented by the class wiring the different format responses
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public interface DataFormatter {
    /**
     * <p>view.</p>
     *
     * @param model a {@link java.lang.Object} object.
     * @return a {@link org.springframework.web.servlet.ModelAndView} object.
     */
    ModelAndView view(Object model);
}

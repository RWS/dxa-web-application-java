package com.sdl.webapp.common.api.formats;

import org.springframework.web.servlet.ModelAndView;

/**
 * DataFormatter, to be implemented by the class wiring the different format responses
 */
public interface DataFormatter {
    ModelAndView view(Object model);
}

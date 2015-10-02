package com.sdl.webapp.common.api.formats;

import com.sdl.webapp.common.api.WebRequestContext;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * DataFormatter, to be implemented by the class wiring the different format responses
 */
public interface DataFormatter {
    ModelAndView view(Object model);
}

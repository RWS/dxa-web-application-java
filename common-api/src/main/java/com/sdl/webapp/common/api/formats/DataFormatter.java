package com.sdl.webapp.common.api.formats;

import com.sdl.webapp.common.api.WebRequestContext;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 9/30/2015.
 */
public interface DataFormatter {
    com.sdl.webapp.common.api.formatters.DataFormatter getFormatter(String format);
}

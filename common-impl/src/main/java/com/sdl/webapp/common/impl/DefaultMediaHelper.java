package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.DefaultImplementation;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.ScreenWidth;
import com.sdl.webapp.common.api.WebRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default implementation of the MediaHelper which can be overriden by addon modules.
 */
@Component
public class DefaultMediaHelper extends DefaultImplementation<MediaHelper> implements MediaHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultMediaHelper.class);

    private final MediaHelper delegatee;
    private final WebRequestContext webRequestContext;

    @Autowired
    public DefaultMediaHelper(WebRequestContext webRequestContext) {
        this.webRequestContext = webRequestContext;
        this.delegatee = new GenericMediaHelper(webRequestContext);
    }

    @Override
    public Class<?> getObjectType() {
        return MediaHelper.class;
    }

    @Override
    public double getDefaultMediaAspect() {
        return delegatee.getDefaultMediaAspect();
    }

    @Override
    public String getDefaultMediaFill() {
        return delegatee.getDefaultMediaFill();
    }

    @Override
    public int getGridSize() {
        return delegatee.getGridSize();
    }

    @Override
    public int getLargeScreenBreakpoint() {
        return delegatee.getLargeScreenBreakpoint();
    }

    @Override
    public int getMediumScreenBreakpoint() {
        return delegatee.getMediumScreenBreakpoint();
    }

    @Override
    public int getResponsiveHeight(String widthFactor, double aspect, int containerSize) {
        return delegatee.getResponsiveHeight(widthFactor, aspect, containerSize);
    }

    @Override
    public String getResponsiveImageUrl(String url, String widthFactor, double aspect, int containerSize) {
        return delegatee.getResponsiveImageUrl(url, widthFactor, aspect, containerSize);
    }

    @Override
    public int getResponsiveWidth(String widthFactor, int containerSize) {
        return delegatee.getResponsiveWidth(widthFactor, containerSize);
    }

    @Override
    public ScreenWidth getScreenWidth() {
        return delegatee.getScreenWidth();
    }

    @Override
    public int getSmallScreenBreakpoint() {
        return delegatee.getSmallScreenBreakpoint();
    }
}

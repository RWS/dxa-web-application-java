package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.api.model.EclUrl;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.entity.GenericWidget;

import javax.servlet.http.HttpServletRequest;

/**
 * Provider for external content provided through an ECL connector
 *
 * @author nic
 */
public interface ExternalContentProvider {

    /**
     * Get model based on an ECL URL
     * @param eclUrl
     * @return model
     * @throws ContentProviderException
     */
    ViewModel getModel(EclUrl eclUrl) throws ContentProviderException;

    /**
     * Get model through a widget and input from the HTTP request
     * @param widget
     * @param request
     * @return model
     * @throws ContentProviderException
     */
    ViewModel getModel(GenericWidget widget, HttpServletRequest request) throws ContentProviderException;
}

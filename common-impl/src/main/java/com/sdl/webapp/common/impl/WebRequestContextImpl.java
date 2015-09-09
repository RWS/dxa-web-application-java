package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.ScreenWidth;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.sdl.webapp.common.api.contextengine.ContextEngine;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.impl.contextengine.BrowserClaims;
import com.sdl.webapp.common.impl.contextengine.ContextEngineImpl;
import com.sdl.webapp.common.impl.contextengine.DeviceClaims;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

/**
 * Implementation of {@code WebRequestContext}.
 *
 * This implementation gets information about the display width etc. from the Ambient Data Framework.
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WebRequestContextImpl implements WebRequestContext {
    private static final Logger LOG = LoggerFactory.getLogger(WebRequestContextImpl.class);
    
    private Localization localization;
    private boolean hasNoLocalization;
    private Integer maxMediaWidth;
    private Double pixelRatio;
    private ScreenWidth screenwidth;
    private boolean contextCookiePresent;
    private Integer displayWidth;
    private String baseUrl;
    private String contextPath;
    private String requestPath;
    private String pageId;
    private Boolean isDeveloperMode;
    
    
    private static final int DEFAULT_WIDTH = 1024;
    private static final int MAX_WIDTH = 1024;
    
    private final MediaHelper mediahelper;
    private final HttpServletRequest request;
    
    @Autowired
    public WebRequestContextImpl(MediaHelper mediaHelper, HttpServletRequest request)
    {
    	this.mediahelper = mediaHelper;
    	this.request = request;
    }
    
    public WebRequestContextImpl()
    {
    	this.request = null;
    	this.mediahelper = null;
    }
    
    @Autowired
    private ContextEngine contextEngine;
    
    @Override
    public Localization getLocalization() {
        return localization;
    }

    @Override
    public void setLocalization(Localization localization) {
        this.localization = localization;
    }
    
    @Override
    public boolean getHasNoLocalization(){
    	return hasNoLocalization;
    }
    
    @Override
    public void setHasNoLocalization(boolean value){
    	hasNoLocalization = value;
    }
    
    @Override
    public int getMaxMediaWidth() {
        if (maxMediaWidth == null) {
            maxMediaWidth = (int) (Math.max(1.0, getPixelRatio()) * Math.min(getDisplayWidth(), MAX_WIDTH));
        }
        return maxMediaWidth;
    }
    
    @Override
    public double getPixelRatio() {
        if (pixelRatio == null) {
            pixelRatio = this.getContextEngine().getClaims(DeviceClaims.class).getPixelRatio();
            if (pixelRatio == null) {
                pixelRatio = 1.0;
                LOG.debug("Pixel ratio ADF claim not available - using default value: {}", pixelRatio);
            }
        }
        return pixelRatio;
    }
 
    public ScreenWidth getScreenWidth()
    {
        if(screenwidth == null)
        {
        	screenwidth = calculateScreenWidth();
        }
        return screenwidth;
    }
    
    @Override
    public boolean isContextCookiePresent() {
        return contextCookiePresent;
    }

    @Override
    public void setContextCookiePresent(boolean present) {
        this.contextCookiePresent = present;
    }
    
    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public String getRequestPath() {
        return requestPath;
    }

    @Override
    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    @Override
    public String getFullUrl() {
        return baseUrl + contextPath + requestPath;
    }
    
    
    @Override
    public ContextEngine getContextEngine() {
       return this.contextEngine;
    }
    
    @Override
    public String getPageId()
    {
    	return pageId;
    }
    
    @Override
    public void setPageId(String value)
    {
    	this.pageId = value;
    }
    
    @Override
    public boolean isDeveloperMode()
    {
        if(this.isDeveloperMode == null){
        	this.isDeveloperMode = getIsDeveloperMode();
        }
        return this.isDeveloperMode();        	
    }

    private boolean getIsDeveloperMode()
    {
    	if(this.request != null && this.request.getRequestURI() != null)
    	{
    		return this.request.getRequestURI().contains("//localhost");
    	}
    	return false;
    }
    
    @Override
    public boolean getIsInclude()
    {
    	return request != null && request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) != null;
    }
    
    @Override
    public boolean isPreview() {
        // Should return true if the request is from XPM (NOTE currently always true for staging as we cannot reliably
        // distinguish XPM requests)
        return localization.isStaging();
    }
    
    @Override
    public int getDisplayWidth(){
    	if (displayWidth == null) {
        	
        	this.displayWidth = this.getContextEngine().getClaims(BrowserClaims.class).getDisplayWidth();
        	if (displayWidth == null) {
                displayWidth = DEFAULT_WIDTH;
            }

            // NOTE: The context engine uses a default browser width of 800, which we override to 1024
            if (displayWidth == 800 && isContextCookiePresent()) {
                displayWidth = DEFAULT_WIDTH;
            }
        }
        return displayWidth;
    }
    
    
    
    protected ScreenWidth calculateScreenWidth()
    {
        int width = isContextCookiePresent() ? this.getDisplayWidth():MAX_WIDTH;
        if (width < this.mediahelper.getSmallScreenBreakpoint())
        {
            return ScreenWidth.EXTRA_SMALL;
        }
        if (width < this.mediahelper.getMediumScreenBreakpoint())
        {
            return ScreenWidth.SMALL;
        }
        if (width < this.mediahelper.getLargeScreenBreakpoint())
        {
            return ScreenWidth.MEDIUM;
        }
        return ScreenWidth.LARGE;
    }
      
}


/*
using System.Net;
using Sdl.Web.Common.Configuration;
using Sdl.Web.Mvc.Context;
using System;
using System.Web;

namespace Sdl.Web.Mvc.Configuration
{
    /// <summary>
    /// Container for request level context data, wraps the HttpContext.Items dictionary, which is used for this purpose
    /// </summary>
    public class WebRequestContext
    {
       
     
    
        /// <summary>
        /// String array of client-supported MIME accept types
        /// </summary>
        public static string[] AcceptTypes
        {
            get 
            { 
                return HttpContext.Current.Request.AcceptTypes;
            }
        }

       

      */
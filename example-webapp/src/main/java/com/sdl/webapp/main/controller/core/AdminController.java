package com.sdl.webapp.main.controller.core;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdminController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    private final WebRequestContext webRequestContext;

    private final LocalizationResolver localizationResolver;

    @Autowired
    public AdminController(WebRequestContext webRequestContext, LocalizationResolver localizationResolver) {
        this.webRequestContext = webRequestContext;
        this.localizationResolver = localizationResolver;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/refresh")
    public String handleRefresh() {
        final Localization localization = webRequestContext.getLocalization();
        LOG.trace("handleRefresh: localization {}", localization.getId());

        localizationResolver.refreshLocalization(localization.getId());

        return "redirect:" + localization.getPath() + "/";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{locPath}/admin/refresh")
    public String handleRefreshLoc(@PathVariable String locPath) {
        return handleRefresh();
    }
}

package com.sdl.webapp.common.controller.api;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.api.navigation.OnDemandNavigationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Handles requests to on-demand navigation API.
 */
@Controller
@RequestMapping(method = RequestMethod.GET, value = {"/api/navigation/subtree", "/{path}/api/navigation/subtree"})
@Profile("dynamic.navigation.provider")
@Slf4j
public class OnDemandNavigationController {

    private final OnDemandNavigationProvider onDemandNavigationProvider;

    private final WebRequestContext webRequestContext;

    @Autowired
    public OnDemandNavigationController(OnDemandNavigationProvider onDemandNavigationProvider, WebRequestContext webRequestContext) {
        this.onDemandNavigationProvider = onDemandNavigationProvider;
        this.webRequestContext = webRequestContext;
    }

    @ResponseBody
    @RequestMapping("/")
    public List<SitemapItem> handle(@RequestParam(value = "includeAncestors", required = false, defaultValue = "false") boolean includeAncestors,
                                    @RequestParam(value = "descendantLevels", required = false, defaultValue = "1") int descendantLevels) {
        return handleInternal(null, includeAncestors, descendantLevels);
    }

    @ResponseBody
    @RequestMapping("/{sitemapItemId}")
    public List<SitemapItem> handle(@PathVariable("sitemapItemId") String sitemapItemId,
                                    @RequestParam(value = "includeAncestors", required = false, defaultValue = "false") boolean includeAncestors,
                                    @RequestParam(value = "descendantLevels", required = false, defaultValue = "1") int descendantLevels) {
        return handleInternal(sitemapItemId, includeAncestors, descendantLevels);
    }

    private List<SitemapItem> handleInternal(String sitemapItemId, boolean includeAncestors, int descendantLevels) {
        if (onDemandNavigationProvider == null) {
            String message = "On-Demand Navigation is not enabled because current navigation provider doesn't support it. " +
                    "If you are using your own navigation provider, you should Implement OnDemandNavigationProvider interface, " +
                    "otherwise check document on how you should enable OnDemandNavigation.";
            log.warn(message);
            throw new UnsupportedOperationException(message);
        }

        NavigationFilter navigationFilter = new NavigationFilter();
        navigationFilter.setWithAncestors(includeAncestors);
        navigationFilter.setDescendantLevels(descendantLevels);
        return onDemandNavigationProvider.getNavigationSubtree(sitemapItemId, navigationFilter, webRequestContext.getLocalization());
    }
}

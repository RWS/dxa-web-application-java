package com.sdl.webapp.common.controller;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.navigation.NavigationFilter;
import com.sdl.webapp.common.api.navigation.OnDemandNavigationProvider;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller that handles API calls.
 */
@Controller
@RequestMapping("/api")
@Slf4j
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class ApiController {

    @Autowired
    private WebRequestContext webRequestContext;

    @Autowired(required = false)
    private OnDemandNavigationProvider onDemandNavigationProvider;

    /**
     * Handles requests to on-demand navigation API.
     *
     * @param sitemapId        not-null siteMapItem ID in a format appropriate for taxonomy (e.g. t1-p2 or t1-k3)
     * @param includeAncestors whether to include ancestors
     * @param descendantLevels requested descendants level
     * @return a JSON representation of a subtree loaded
     * @throws UnsupportedOperationException is dynamic navigation s not enabled
     */
    @RequestMapping(method = RequestMethod.GET, value = "/navigation/subtree/{sitemapId}")
    @ResponseBody
    public List<SitemapItem> handleGetNavigationSubtree(@PathVariable @NotNull String sitemapId,
                                                        @RequestParam("includeAncestors") boolean includeAncestors,
                                                        @RequestParam(value = "descendantLevels", required = false, defaultValue = "1") int descendantLevels) {
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
        return onDemandNavigationProvider.getNavigationSubtree(sitemapId, navigationFilter, webRequestContext.getLocalization());
    }


}

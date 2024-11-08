package org.example.controller;

import org.example.service.AdminService;

import com.google.common.collect.Iterators;

import com.sdl.dxa.caching.NamedCacheProvider;
import com.sdl.dxa.caching.statistics.CacheStatisticsProvider;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Admin controller that provides access for administrator.
 */
@Slf4j
@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Autowired(required = false)
    private NamedCacheProvider cacheProvider;

    @Autowired(required = false)
    private CacheStatisticsProvider cacheStatisticsProvider;

    /**
     * Refreshes the current localization and redirects to the given path.
     *
     * @return the redirect command for Spring MVC
     */
    @RequestMapping(method = RequestMethod.GET, value = {"/admin/refresh", "/*/admin/refresh"})
    public String handleRefresh() {
        if (cacheManager != null) {
            cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
        }
        return "redirect:" + adminService.refreshLocalization();
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/admin/cache-stats", "/*/admin/cache-stats"})
    @ResponseBody
    public List<CacheStatistic> cacheStatistics() {
        List<CacheStatistic> items = new ArrayList<>();
        if (cacheProvider != null && cacheProvider.isCacheEnabled() && cacheStatisticsProvider != null) {
            cacheProvider.getCacheManager().getCacheNames().forEach(name -> {
                Cache<Object, Object> cache = cacheProvider.getCacheManager().getCache(name);
                CacheStatistic cacheStatistic = new CacheStatistic();
                cacheStatistic.setCacheName(name);
                cacheStatistic.setCurrentNumberOfEntriesInCache(Iterators.size(cache.iterator()));
                cacheStatistic.setStatistics(cacheStatisticsProvider.getStatistics(name));
                items.add(cacheStatistic);
            });
        }
        return items;
    }

    @Data
    public static class CacheStatistic {
        private String cacheName;
        private Integer currentNumberOfEntriesInCache;
        private Map<String, Long> statistics;
    }
}

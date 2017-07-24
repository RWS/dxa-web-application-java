package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.cache.CacheManager;

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
}

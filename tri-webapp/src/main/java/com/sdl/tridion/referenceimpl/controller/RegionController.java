package com.sdl.tridion.referenceimpl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/region")
public class RegionController {
    private static final Logger LOG = LoggerFactory.getLogger(RegionController.class);

    private static final String REGION_VIEW_PREFIX = "region/";

    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    public String handleGetRegion(@PathVariable("name") String name) {
        LOG.debug("handleGetRegion: name={}", name);
        return REGION_VIEW_PREFIX + name;
    }
}

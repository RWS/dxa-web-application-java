package com.sdl.tridion.referenceimpl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/entity")
public class EntityController {
    private static final Logger LOG = LoggerFactory.getLogger(EntityController.class);

    private static final String ENTITY_VIEW_PREFIX = "entity/";

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public String handleGetEntity(@PathVariable("id") String id) {
        LOG.debug("handleGetEntity: id={}", id);
        return ENTITY_VIEW_PREFIX + id;
    }
}

package com.sdl.tridion.referenceimpl.controller;

import com.sdl.tridion.referenceimpl.controller.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class EntityController {
    private static final Logger LOG = LoggerFactory.getLogger(EntityController.class);

    private static final String ENTITY_VIEW_PREFIX = "entity/";

    @RequestMapping(method = RequestMethod.GET, value = "/entity/{id}")
    public String handleGetEntity(HttpServletRequest request, @PathVariable("id") String id) {
        LOG.debug("handleGetEntity: id={}", id);

        if (request.getAttribute(JspBeanNames.PAGE_MODEL) == null) {
            throw new BadRequestException("Missing page model");
        }

        return ENTITY_VIEW_PREFIX + id;
    }
}

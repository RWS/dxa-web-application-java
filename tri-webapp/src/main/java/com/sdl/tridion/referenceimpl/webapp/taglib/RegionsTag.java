package com.sdl.tridion.referenceimpl.webapp.taglib;

import com.google.common.base.Strings;
import com.sdl.tridion.referenceimpl.common.model.Page;
import com.sdl.tridion.referenceimpl.common.model.Region;
import com.sdl.tridion.referenceimpl.webapp.controller.core.ViewAttributeNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RegionsTag extends TagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(RegionsTag.class);

    private String exclude;

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    @Override
    public int doStartTag() throws JspException {
        final Page page = (Page) pageContext.getRequest().getAttribute(ViewAttributeNames.PAGE_MODEL);
        if (page == null) {
            LOG.debug("Page not found in request attributes");
            return SKIP_BODY;
        }

        Set<String> excludes = new HashSet<>();
        if (!Strings.isNullOrEmpty(exclude)) {
            excludes.addAll(Arrays.asList(exclude.split("\\s*,\\s*")));
        }

        for (Region region : page.getRegions().values()) {
            String name = region.getName();
            if (excludes.contains(name)) {
                LOG.debug("Excluding region: {}", name);
                continue;
            }

            if (page.getRegions().containsKey(name)) {
                LOG.debug("Including region: {}", name);
                try {
                    pageContext.include("/region/" + name);
                } catch (ServletException | IOException e) {
                    throw new JspException("Error while processing regions tag", e);
                }
            } else {
                LOG.debug("Region not found: {}", name);
            }
        }

        return SKIP_BODY;
    }
}

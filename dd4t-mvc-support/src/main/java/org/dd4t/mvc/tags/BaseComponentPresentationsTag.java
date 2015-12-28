/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.mvc.tags;

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.util.Constants;
import org.dd4t.mvc.utils.RenderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Components presentation rendering tag.
 */
public abstract class BaseComponentPresentationsTag extends SimpleTagSupport {

    private static final Logger LOG = LoggerFactory.getLogger(BaseComponentPresentationsTag.class);
    private static final String ANCHOR_FORMAT = "<a name=\"%d\"></a>\n";

    private String schema;
    private String rootElement;
    private String view;
    private Boolean addAnchor = false;
    private Integer start;
    private Integer end;
    private String region;

    public int getAnchorCount (HttpServletRequest request) {
        int counter = 0;

        if (null != request.getAttribute("anchorCounter")) {
            counter = (Integer) request.getAttribute("anchorCounter") + 1;
        }
        request.setAttribute("anchorCounter", counter);
        return counter;
    }

    protected abstract List<ComponentPresentation> getComponentPresentations (Page page);

    @Override
    public void doTag () throws JspException, IOException {
        final Page page = (Page) getJspContext().getAttribute(Constants.PAGE_MODEL_KEY, PageContext.REQUEST_SCOPE);

        if (page != null) {
            final PageContext pageContext = (PageContext) getJspContext();
            List<ComponentPresentation> filteredComponentPresentations = RenderUtils.filterComponentPresentations(getComponentPresentations(page), getSchema(), getRootElement(), getView(), this.getRegion());
            String out = "";

            if (start != null || end != null) {
                int size = filteredComponentPresentations.size();
                int startPos = (start != null) ? start : 0;
                int endPos = (end != null) ? end : size;

                if (startPos <= endPos && startPos <= size && endPos <= size) {
                    filteredComponentPresentations = filteredComponentPresentations.subList(startPos, endPos);
                } else {
                    LOG.error("start {} and end {} filtering incorrect for number of component presentations ({}) on page {}", size, page.getId());
                }
            }

            final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            final HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

            try {
                out = RenderUtils.renderComponentPresentations(request, response, filteredComponentPresentations);
            } catch (FactoryException e) {
                LOG.error(e.getLocalizedMessage(), e);
            }

            if (addAnchor) {
                pageContext.getOut().write(String.format(ANCHOR_FORMAT, getAnchorCount(request)) + out);
            } else {
                pageContext.getOut().write(out);
            }
        } else {
            LOG.warn("The JSP context does not contain an attribute called '" + Constants.PAGE_MODEL_KEY + "'.");
        }
    }

    public String getSchema () {
        return schema;
    }

    public void setSchema (final String schema) {
        this.schema = schema.toLowerCase(Locale.getDefault());
    }

    public String getRootElement () {
        return rootElement;
    }

    public void setRootElement (final String rootElement) {
        this.rootElement = rootElement;
    }

    public String getView () {
        return view;
    }

    public void setView (final String view) {
        this.view = view.toLowerCase(Locale.getDefault());
    }

    public Boolean isAddAnchor () {
        return addAnchor;
    }

    public void setAddAnchor (final Boolean addAnchor) {
        this.addAnchor = addAnchor;
    }

    public void setStart (final Integer start) {
        this.start = start;
    }

    public void setEnd (final Integer end) {
        this.end = end;
    }

    public String getRegion () {
        return region;
    }

    public void setRegion (final String region) {
        this.region = region;
    }
}

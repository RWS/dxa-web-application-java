package org.dd4t.mvc.tags;

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

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public class RenderDynamicComponentPresentationTag extends SimpleTagSupport {
    private static final Logger LOG = LoggerFactory.getLogger(RenderDynamicComponentPresentationTag.class);
    private String componentURI;
    private String templateURI;
    private String viewName;

    @Override
    public void doTag () throws JspException, IOException {

        final Page page = (Page) getJspContext().getAttribute(Constants.PAGE_MODEL_KEY, PageContext.REQUEST_SCOPE);

        if (page != null) {
            final PageContext pageContext = (PageContext) getJspContext();
            final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            final HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

            String renderedCp = "";
            try {
                renderedCp = RenderUtils.renderDynamicComponentPresentation(request, response, componentURI, templateURI, viewName);
            } catch (FactoryException e) {
                LOG.error(e.getLocalizedMessage(), e);
            }

            pageContext.getOut().write(renderedCp);
        } else {
            LOG.warn("The JSP context does not contain an attribute called '" + Constants.PAGE_MODEL_KEY + "'.");
        }
    }

    public String getComponentURI () {
        return componentURI;
    }

    public void setComponentURI (final String componentURI) {
        this.componentURI = componentURI;
    }

    public String getTemplateURI () {
        return templateURI;
    }

    public void setTemplateURI (final String templateURI) {
        this.templateURI = templateURI;
    }

    public String getViewName () {
        return viewName;
    }

    public void setViewName (final String viewName) {
        this.viewName = viewName;
    }
}

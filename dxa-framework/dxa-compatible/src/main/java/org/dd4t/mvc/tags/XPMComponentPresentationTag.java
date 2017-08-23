package org.dd4t.mvc.tags;

import org.dd4t.core.databind.TridionViewModel;
import org.dd4t.core.util.Constants;
import org.dd4t.mvc.utils.XPMRenderer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * Implementation of the ComponentsPresentation tag. This implementation aligns
 * with the .Net implementation which is part of the C# dd4t package.
 *
 * @author m07c315
 */
public class XPMComponentPresentationTag extends SimpleTagSupport {

    protected TridionViewModel model;

    @Override
    public void doTag () throws JspException, IOException {
        final PageContext pageContext = (PageContext) getJspContext();
        final String templateId = (String) pageContext.getAttribute(Constants.COMPONENT_TEMPLATE_ID, PageContext.REQUEST_SCOPE);
        final boolean isDynamic = (boolean) pageContext.getAttribute(Constants.DYNAMIC_COMPONENT_PRESENTATION, PageContext.REQUEST_SCOPE);

        XPMRenderer renderer = XPMRenderer.getInstance();

        final String out = renderer.componentPresentation(model.getTcmUri().toString(), model.getLastModified(), templateId, isDynamic);
        pageContext.getOut().write(out);
    }

    public TridionViewModel getModel () {
        return model;
    }

    public void setModel (TridionViewModel model) {
        this.model = model;
    }
}

package org.dd4t.mvc.tags;

import org.dd4t.contentmodel.Page;
import org.dd4t.mvc.utils.XPMRenderer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public class XPMPageInitTag extends SimpleTagSupport {

    private Page page;

    @Override
    public void doTag () throws JspException, IOException {
        XPMRenderer renderer = XPMRenderer.getInstance();
        String out = renderer.initPage(page.getId(), page.getRevisionDate(), page.getPageTemplate().getId());
        getJspContext().getOut().write(out);
    }

    public org.dd4t.contentmodel.Page getPage () {
        return page;
    }

    public void setPage (Page page) {
        this.page = page;
    }
}

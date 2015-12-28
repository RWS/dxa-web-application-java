package org.dd4t.mvc.tags;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.core.databind.TridionViewModel;
import org.dd4t.mvc.utils.XPMRenderer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

/**
 * Implementation of the Component Field tag. This implementation aligns
 * with the .Net implementation which is part of the C# dd4t package.
 *
 * @author m07c315
 */
public class XPMComponentFieldTag extends BodyTagSupport {

    private transient TridionViewModel model;
    private String field;
    private int index;
    private String enclosed = "span";
    private Boolean useEnclosingTags = true;

    @Override
    public int doAfterBody () throws JspException {
        XPMRenderer factory = XPMRenderer.getInstance();
        BodyContent content = getBodyContent();
        StringBuilder out = new StringBuilder();

        if (XPMRenderer.isXPMEnabled()) {

            if (model == null) {
                return SKIP_BODY;
            }

            String xpath;
            xpath = model.getXPath(field);
            boolean isMultiValued = model.isMultiValued(field);

            if (StringUtils.isNotEmpty(enclosed) && useEnclosingTags) {
                out.append(String.format("<%s>", enclosed));
            }

            out.append(factory.componentField(xpath, isMultiValued, index));
            out.append(content.getString());

            if (StringUtils.isNotEmpty(enclosed) && useEnclosingTags) {
                out.append(String.format("</%s>", enclosed));
            }
        } else {
            out.append(content.getString());
        }

        try {
            JspWriter writer = content.getEnclosingWriter();
            writer.write(out.toString());
        } catch (IOException e) {
            throw new JspException("Failed to write body content", e);
        }

        return SKIP_BODY;
    }

    public TridionViewModel getModel () {
        return model;
    }

    public void setModel (TridionViewModel model) {
        this.model = model;
    }

    public String getField () {
        return field;
    }

    public void setField (String field) {
        this.field = field;
    }

    public int getIndex () {
        return index;
    }

    public void setIndex (int index) {
        this.index = index;
    }

    public String getEnclosed () {
        return enclosed;
    }

    public void setEnclosed (String enclosed) {
        this.enclosed = enclosed;
    }

    public Boolean isUseEnclosingTags () {
        return useEnclosingTags;
    }

    public void setUseEnclosingTags (final Boolean useEnclosingTags) {
        this.useEnclosingTags = useEnclosingTags;
    }
}

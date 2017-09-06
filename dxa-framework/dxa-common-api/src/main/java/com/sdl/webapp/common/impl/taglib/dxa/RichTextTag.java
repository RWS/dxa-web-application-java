package com.sdl.webapp.common.impl.taglib.dxa;

import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.RichTextFragment;
import com.sdl.webapp.common.controller.ControllerUtils;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.AbstractMarkupTag;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

@Setter
public class RichTextTag extends AbstractMarkupTag {
    private static final Logger LOG = LoggerFactory.getLogger(RichTextTag.class);

    private RichText content;

    /**
     * {@inheritDoc}
     */
    @Override
    public int doStartTag() throws JspException {

        final JspWriter out = pageContext.getOut();
        StringBuilder builder = new StringBuilder(2048);
        try {
            for (RichTextFragment fragment : content.getFragments()) {
                EntityModel entityModel = (fragment instanceof EntityModel ? (EntityModel) fragment : null);
                String htmlFragment;
                if (entityModel == null) {
                    htmlFragment = fragment.toHtmlElement().toHtml();
                } else {
                    try {
                        this.pageContext.getRequest().setAttribute("_entity_", entityModel);
                        htmlFragment = this.processInclude(ControllerUtils.getIncludePath(entityModel), entityModel);
                    } catch (ServletException e) {
                        throw new JspException(e);
                    }
                }

                builder.append(htmlFragment);
            }

            out.write(builder.toString());
        } catch (IOException | DxaException e) {
            LOG.error("Error while rendering rich text", e);
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

}

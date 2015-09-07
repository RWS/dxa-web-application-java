package com.sdl.webapp.main.taglib.dxa;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.RichTextFragment;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.controller.ControllerUtils;
import com.sdl.webapp.common.markup.AbstractMarkupTag;

public class RichTextTag extends AbstractMarkupTag {
    private static final Logger LOG = LoggerFactory.getLogger(RichTextTag.class);

    private RichText content;
    
    public void setContent(RichText content) {
        this.content = content;
    }
    @Override
    public int doStartTag() throws JspException {
    	
    	 final JspWriter out = pageContext.getOut();
    	 StringBuilder builder = new StringBuilder();
         try {
        	 for(RichTextFragment fragment: content.getFragments())
        	 {
        		 EntityModel entityModel = (fragment instanceof EntityModel ? (EntityModel)fragment : null);
        		 String htmlFragment = "";
        		 if(entityModel == null)
        		 {
        			 htmlFragment = fragment.toHtml();
        		 }
        		 else
        		 {
        			 try {
        				 this.pageContext.getRequest().setAttribute("_entity_" + entityModel.getId(), entityModel);
						htmlFragment = this.processInclude(ControllerUtils.getIncludePath(entityModel), entityModel);
					} catch (ServletException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		 }
                 
                 builder.append(htmlFragment);
        	 }
        	 
             out.write(builder.toString());
         } catch (IOException e) {
             throw new JspException(e);
         }
         return SKIP_BODY;
    }
    
}

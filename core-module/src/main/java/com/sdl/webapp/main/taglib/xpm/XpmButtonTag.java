package com.sdl.webapp.main.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.markup.AbstractMarkupTag;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlMultiNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import com.sdl.webapp.common.controller.ControllerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

import java.io.IOException;
import java.util.UUID;

import static com.sdl.webapp.common.controller.RequestAttributeNames.PAGE_MODEL;

public class XpmButtonTag extends XpmMarkupTag {
    private static final Logger LOG = LoggerFactory.getLogger(XpmButtonTag.class);

    private RegionModel region;
    
    public void setRegion(RegionModel region)
    {
    	this.region = region;
    }
    private boolean isInclude()
    {
    	return pageContext.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) != null;
    }
	@Override
	protected HtmlNode generateXpmMarkup() {

		if(isInclude())
		{
			String title = "Go Back";
			String editUrl = "javascript:history.back()";
			   return HtmlBuilders.div()
					   	.withClass("xpm-button")
		                .withContent(HtmlBuilders.a(editUrl)
	                    				.withClass("fa-stack fa-lg")
	                    				.withTitle(title)
	                    				.withContent(
	                    						new HtmlMultiNode(
	                    								HtmlBuilders.i().withClass("fa fa-square fa-stack-2x").build(), 
	                    								HtmlBuilders.i().withClass("fa fa-arrow-left fa-inverse fa-stack-1x").build())
	                    						)
	                					.build())
		                .build();
		}
		else
		{
			String path = this.pageContext.getServletContext().getContextPath();
			String title = "Edit " + this.region.getXpmMetadata().get(RegionModelImpl.IncludedFromPageTitleXpmMetadataKey);
			String editUrl = "/" + path + this.region.getXpmMetadata().get(RegionModelImpl.IncludedFromPageFileNameXpmMetadataKey);
		   return HtmlBuilders.div()
				   	.withClass("xpm-button")
	                .withContent(HtmlBuilders.a(editUrl)
                    				.withClass("fa-stack fa-lg")
                    				.withTitle(title)
                    				.withContent(
                    						new HtmlMultiNode(
                    								HtmlBuilders.i().withClass("fa fa-square fa-stack-2x").build(), 
                    								HtmlBuilders.i().withClass("fa fa-pencil fa-inverse fa-stack-1x").build())
                    						)
                					.build())
	                .build();
		}
		/*
		 * @model RegionModel
@if (WebRequestContext.IsInclude)
{
    <div class="xpm-button">
        <a href="javascript:history.back()" title="Go Back" class="fa-stack fa-lg">
            <i class="fa fa-square fa-stack-2x"></i>
            <i class="fa fa-arrow-left fa-inverse fa-stack-1x"></i>
        </a>
    </div>
}
else
{
    <div class="xpm-button">
        <a href="/system/include/@Model.XpmMetadata[RegionModel.IncludedFromPageFileNameXpmMetadataKey]" title="Edit @Model.XpmMetadata[RegionModel.IncludedFromPageTitleXpmMetadataKey]" class="fa-stack fa-lg">
            <i class="fa fa-square fa-stack-2x"></i>
            <i class="fa fa-pencil fa-inverse fa-stack-1x"></i>
        </a>
    </div>
}

		 * 
		 */
		
	}

   
  
}

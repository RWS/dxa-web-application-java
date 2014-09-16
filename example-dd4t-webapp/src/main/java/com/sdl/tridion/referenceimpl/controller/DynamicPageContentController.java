/**  
 *  Copyright 2011 Capgemini & SDL
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.sdl.tridion.referenceimpl.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.GenericPage;
import com.sdl.tridion.referenceimpl.siteedit.SiteEditService;
import com.sdl.tridion.referenceimpl.view.IViewHandler;
import com.sdl.tridion.referenceimpl.view.model.ComponentViews;
import com.sdl.tridion.referenceimpl.view.model.RenderedComponent;
import com.sdl.tridion.referenceimpl.view.model.ViewRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;


public class DynamicPageContentController extends BaseDD4TController implements
        ContentController {
    private static Logger logger = LoggerFactory
            .getLogger(DynamicPageContentController.class);

    @Autowired
    private IViewHandler<Component> componentViewManager;

    public IViewHandler<Component> getComponentViewManager() {

        return componentViewManager;
    }

    public void setComponentViewManager(
            IViewHandler<Component> componentViewManager) {

        this.componentViewManager = componentViewManager;
    }

    /**
     * Function builds a ComponentViews model based on given pagemodel
     */
    public ComponentViews buildComponentViews(GenericPage model,
            HttpServletRequest req, HttpServletResponse res) throws Exception {

        ComponentViews viewmodel = new ComponentViews();

        int order = 1;
        for (ComponentPresentation cp : model.getComponentPresentations()) {
            if (logger.isDebugEnabled()){
                logger.debug("found cp with ct " + cp.getComponentTemplate().getId());
            }
            logger.debug("DynamicPageContentController: componentPresentation " +cp.toString());      //CPL
            String region = getRegionFromTemplate(cp.getComponentTemplate());
            if (!viewmodel.getRegions().containsKey(region)) {
                viewmodel.getRegions().put(region, new ViewRegion());
            }
            
            if (logger.isDebugEnabled()){
                logger.debug("using region " + region);
            }
            
            // attempt to load the view result from the rendered content
            String viewresult = cp.getRenderedContent();

            // if successfull, no need to dispatch
            if (viewresult != null && viewresult.length()>0) {
            	if (logger.isDebugEnabled()){
            	    logger.debug("found cp with (pre)rendered content");
            	}
            } 
            // otherwise, we'll need to call the viewManager
            else {
                String view = getViewFromTemplate(cp.getComponentTemplate());	
                if (logger.isDebugEnabled()){
                    logger.debug("using view " + view);
                }
                
                viewresult = buildComponentView(model, req, res, order, cp, region, view);
            }            

            // TODO - Would really like to build the regions from the rendered components, but is this in conflict with the origin source?
            viewmodel.addRenderedComponent(region, new RenderedComponent(viewresult, region, cp, order));
            viewmodel.getRegions().get(region).getComponentViews()
                    .add(viewresult);
            
            order++;
        }

        return viewmodel;
    }

	protected String buildComponentView(GenericPage model, HttpServletRequest req, HttpServletResponse res, int order, ComponentPresentation cp, String region, String view) throws Exception {
		// add the site edit String to the generated HTML
		String se = SiteEditService.generateSiteEditComponentTag(cp, order, region, req);
          
		return "<div>" + se
				+ componentViewManager.handleView(model, cp.getComponent(), view, req, res)
				+ "</div>";
	}


    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

    	return null;
    }
}

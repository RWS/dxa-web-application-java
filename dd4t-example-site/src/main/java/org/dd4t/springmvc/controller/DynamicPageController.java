/*
 * Copyright (c) 2015 R. Oudshoorn
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
package org.dd4t.springmvc.controller;

import org.dd4t.contentmodel.Page;
import org.dd4t.core.factories.PageFactory;
import org.dd4t.springmvc.constants.Constants;
import org.dd4t.springmvc.view.IViewHandler;
import org.dd4t.springmvc.view.model.ComponentViews;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DynamicPageController extends BaseDD4TController {
    private static Logger logger = LoggerFactory.getLogger(DynamicPageController.class);
    
    private PageFactory genericPageFactory;

    //private SimplePageFactory simplePageFactory;
    
    private ContentController contentController;
    
    private IViewHandler<Page> pageViewHandler;
    
    public IViewHandler<Page> getPageViewHandler() {
		return pageViewHandler;
	}

	public void setPageViewHandler(IViewHandler<Page> pageViewHandler) {
		this.pageViewHandler = pageViewHandler;
	}

	private int publication;
    
    private String subcontext;

    public String getSubcontext() {
    
        return subcontext;
    }

    public void setSubcontext(String subcontext) {
    
        this.subcontext = subcontext;
    }

    public int getPublication() {
    
        return publication;
    }

    public void setPublication(int publication) {
    
        this.publication = publication;
    }

    public ContentController getContentController() {

        return contentController;
    }

    public void setContentController(ContentController contentController) {

        this.contentController = contentController;
    }

    /*
    public SimplePageFactory getSimplePageFactory() {
        
        return simplePageFactory;
    }

    public void setSimplePageFactory(SimplePageFactory simplePageFactory) {
    
        this.simplePageFactory = simplePageFactory;
    }*/
    
    public PageFactory getGenericPageFactory() {

        return genericPageFactory;
    }

    public void setGenericPageFactory(PageFactory genericPageFactory) {

        this.genericPageFactory = genericPageFactory;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        long start = System.currentTimeMillis();     
        
        String URL = request.getRequestURI();
        
        if(logger.isDebugEnabled())
            logger.debug("Received request in MAV " + URL);

        URL = URL.replaceFirst(request.getContextPath(), "");

        if(logger.isDebugEnabled())
            logger.debug("Request shortened to " + URL);

        Page pageModel = genericPageFactory.findPageByUrl(URL, publication);
      //          new BasicRequestContext(request));
      
        request.setAttribute(Constants.PAGE_MODEL_KEY, pageModel);      

        long pagemodeldone = System.currentTimeMillis();
        
        if(logger.isDebugEnabled())
            logger.debug("Built pageModel for page: " + pageModel.getTitle() +" in "+(pagemodeldone-start)+" milliseconds.");

        ComponentViews contentModel =
                contentController.buildComponentViews(pageModel, request,
                        response);

        long contentmodeldone = System.currentTimeMillis();
        
        request.setAttribute(Constants.CONTENT_MODEL_KEY, contentModel);        
        
        if(logger.isDebugEnabled())
            logger.debug("Built contentModel: " + contentModel+" in "+(contentmodeldone-pagemodeldone)+" milliseconds.");

        /*
        Page navigationModel = getNavigationModel(pageModel);
        
        long navmodeldone = System.currentTimeMillis();
        
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Built navigationmodel: " + navigationModel+" in "+(navmodeldone-contentmodeldone)+" milliseconds.");
        }  
        request.setAttribute(Constants.NAVIGATION_MODEL_KEY, navigationModel);  
          */
          
        // attain view
        String view = getViewFromTemplate(pageModel.getPageTemplate());
        
        // render page through the viewhandler
        String rendered_page = pageViewHandler.handleView(pageModel, pageModel, view, request, response);
        
        // and write to the response (!)
        response.getWriter().write(rendered_page);
        
        if(logger.isInfoEnabled()){
            long end = System.currentTimeMillis();        
            logger.info("Built pageresponse in " + (end - start) + " milliseconds.");
        }
        
        return null;        
    }    
    
    /*
    public Page getNavigationModel(Page page){
        try{
            return simplePageFactory.findPageByUrl(subcontext + Constants.NAVIGATION_PAGE,publication);
        }
        catch(Exception ex){
            logger.error("Unable to retrieve navigationmodel: "+ex.getMessage(), ex);
        }
        
        return null;
    }*/
}

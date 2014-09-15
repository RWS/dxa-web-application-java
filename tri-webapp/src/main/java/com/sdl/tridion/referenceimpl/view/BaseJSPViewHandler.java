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
package com.sdl.tridion.referenceimpl.view;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dd4t.contentmodel.GenericPage;
import com.sdl.tridion.referenceimpl.constants.Constants;
import com.sdl.tridion.referenceimpl.view.model.CharResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class manages a directory full of files. Both directory and fileending are
 * given by implementing class, and the baseviewhandler will index all
 * appropriate files and deliver them to the implementing class inside of the
 * cachedviews Map in the form of [viewname] : [absolute path to file]
 * 
 * @author rooudsho
 * 
 */
public abstract class BaseJSPViewHandler<T> implements IViewHandler<T>{
    private static Logger logger = LoggerFactory.getLogger(BaseJSPViewHandler.class);

    /**
     * Local set of cachedviews, enabling speedy lookup of views.
     */
    protected Map<String, String> cachedViews;    
    /**
     * Filepattern, determines where the JSP view handler will attempt to dispatch the request to. The pattern will be formatted using String.format(), given
     * the vievname as the first parameter.
     */
    private String filePattern;
    /**
     * Setter for the filePattern.
     * 
     * @param filePattern The pattern, used in String.format
     */
    public void setFilePattern(String filePattern) {
    
        this.filePattern = filePattern;
    }
    /**
     * modelKey is the key given to the view for the model. As this is a parametric class, the type of model depends on the implementation.
     */
    private String modelKey;
    /**
     * Setter for the modelKey.
     * 
     * @param modelKey The key, usable from within the JSP file
     */
    public void setModelKey(String modelKey) {
    
        this.modelKey = modelKey;
    }

    /**
     * The Constructor.
     * 
     */
    public BaseJSPViewHandler() {

        cachedViews = new HashMap<String, String>();
    }
    
    /**
     * Simple getter to expose the views the dispatcher has proven to be able to handle.
     * 
     * {@inheritDoc}
     */
    public Collection<String> provideViews() {
        return cachedViews.keySet();
    }
    
    /**
     * 
     * Function determines the dispatchable URL for a given view, by calling String.format on the filePattern with the view as parameter.
     * 
     * @param view The (String) name of the view.
     * @return A dispatchable URL
     */
    protected String getResourceURLForView(String view){
    	
    	// if the view starts with a slash, it's an application
    	if(view.startsWith("/")){
    		return view;
    	}
    	
        return String.format(filePattern, view);
    }

    /**
     * Function determines if this handler is able to process a given view.
     * 
     * {@inheritDoc}
     */
    public boolean canHandleView(String view,
            HttpServletRequest req, HttpServletResponse res){
        if(filePattern == null && !cachedViews.containsKey(view))
            return false;
        
        if(!cachedViews.containsKey(view)){
            String URL = getResourceURLForView(view);
            
            try{                               
                dispatch(null, null, URL, req, res);
                
                if(logger.isDebugEnabled())
                    logger.debug("Dispatch successfull, adding view for viewid "+view);
                
                cachedViews.put(view, URL);
                
                return true;
            } catch (FileNotFoundException ex) {
                if(logger.isDebugEnabled())
                    logger.debug("Dispatch unsuccessfull, ignoring view: "+ ex.getMessage());
                
                return false;
            }                
            catch(Exception ex){
                if(logger.isDebugEnabled())
                    logger.debug("Dispatch unsuccessfull due to try-catch unaware view, using view: "+ ex.getMessage());
                
                cachedViews.put(view, URL);
                
                return true;
            }
        }
        else{
            return true;
        }
    }
    
    /**
     * Function dispatches given model to given viewURL.
     * 
     * @param URL The relative URL to which the requestdispatcher includes
     * @param model The model, stored in the request
     * @param req The request to dispatch
     * @param res The response which will be wrapper for the the include
     * @return String representation of the view result
     * @throws Exception The dispatch can throw a variety of exceptions, such as notably a FileNotFoundException when the request cannot be dispatched
     */
    private String dispatch(GenericPage page, T model,String URL,   HttpServletRequest req, HttpServletResponse res) throws Exception {
        if(logger.isDebugEnabled())
            logger.debug("Attempting to dispatch view to "+URL);
                        
        // retrieve dispatcher to component JSP view
        RequestDispatcher dispatcher =
                req.getSession()
                        .getServletContext()
                        .getRequestDispatcher(
                                URL);               
        
        // create wrapper (acts as response, but stores output in a
        // CharArrayWriter)
        CharResponseWrapper wrapper = new CharResponseWrapper(res);
        // run the include
        dispatcher.include(req, wrapper);
        
        return wrapper.toString();
    }
    
    /**
     * Function handles the view by using the internal dispatch(..) function.
     * 
     * {@inheritDoc}
     */
    @Override
    public String handleView(GenericPage page, T model, String ViewID, HttpServletRequest req, HttpServletResponse res) throws Exception {

        if(!canHandleView(ViewID, req, res))
            throw new Exception("Can not include unknown view with ID "+ViewID);
 
        try {
            req.setAttribute(modelKey, model);
            req.setAttribute(Constants.PAGE_MODEL_KEY, page);
            return dispatch(page, model,cachedViews.get(ViewID),  req, res);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return ex.getMessage();
        }
    }
}

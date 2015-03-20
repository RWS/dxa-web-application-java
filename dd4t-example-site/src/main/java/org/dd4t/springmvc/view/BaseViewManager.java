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
package org.dd4t.springmvc.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dd4t.contentmodel.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class, wiring different viewhandlers together to act as a single
 * handler. Different handlers can be wired, and they will be polled for
 * viewrequests to determine if there is one to handle it.
 * 
 * @author rooudsho
 * 
 * @param <T>
 */
public abstract class BaseViewManager<T> implements IViewManager<T> {
    private static Logger logger = LoggerFactory.getLogger(BaseViewManager.class);

    private List<IViewHandler<T>> handlers;

    private Map<String, IViewHandler<T>> cachedHandlers;

    public BaseViewManager() {

        cachedHandlers = new HashMap<String, IViewHandler<T>>();
    }

    /**
     * Function determines if there is a handler in our wiring able to handle
     * the view for given viewname; handler is returned and the polling result
     * is cached for performance reasons.
     * 
     * @param viewname
     * @return
     */
    protected IViewHandler<T> determineHandler(String viewname,
            HttpServletRequest req, HttpServletResponse res) {

        IViewHandler<T> handler = null;

        if (logger.isDebugEnabled())
            logger.debug("Searching for handler for view " + viewname);

        if (cachedHandlers.containsKey(viewname)) {
            if (logger.isDebugEnabled())
                logger.debug("Found view in cache, returning");

            handler = cachedHandlers.get(viewname);
        } else {
            for (IViewHandler<T> thishandler : handlers) {
                if (logger.isDebugEnabled())
                    logger.debug("Searching for view in "+ thishandler);

                if (thishandler.canHandleView(viewname, req, res)) {
                    handler = thishandler;
                    cachedHandlers.put(viewname, handler);
                }
            }
        }

        return handler;
    }
    
    @Override
    public boolean canHandleView(String view,
            HttpServletRequest req, HttpServletResponse res){
        return determineHandler(view, req, res) != null;
    }

    @Override
    public String handleView(Page pagemodel, T model, String ViewID, HttpServletRequest req,
            HttpServletResponse res) throws Exception {

        IViewHandler<T> handler = determineHandler(ViewID, req, res);

        if (handler == null) {
            logger.error("Unable to find view for ID " + ViewID);
            return "";
        }

        return handler.handleView(pagemodel, model, ViewID, req, res);
    }

    public void setHandlers(List<IViewHandler<T>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public Collection<String> provideViews() {
        return cachedHandlers.keySet();
    }
}

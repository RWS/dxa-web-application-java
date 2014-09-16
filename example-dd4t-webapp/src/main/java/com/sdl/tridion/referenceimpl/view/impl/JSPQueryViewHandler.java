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
package com.sdl.tridion.referenceimpl.view.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.impl.NumericField;
import org.dd4t.core.factories.ComponentFactory;
import com.sdl.tridion.referenceimpl.view.BaseViewHandler;
import com.sdl.tridion.referenceimpl.view.IViewHandler;
import com.sdl.tridion.referenceimpl.view.model.CharResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tridion.broker.querying.Query;
import com.tridion.broker.querying.criteria.Criteria;
import com.tridion.broker.querying.criteria.content.ItemSchemaCriteria;
import com.tridion.broker.querying.filter.LimitFilter;

public class JSPQueryViewHandler extends BaseViewHandler implements
        IViewHandler<Component> {
    private static Logger logger = LoggerFactory
            .getLogger(JSPQueryViewHandler.class);

    
    @Autowired
    private ComponentFactory componentFactory;    
    
    public JSPQueryViewHandler() {

        // super(JSPPath, "jsp");
        super();
        cachedViews.put("queryresults", "queryresults");
    }

    @Override
    public String handleView(GenericPage page, Component model, String ViewID,
            HttpServletRequest req, HttpServletResponse res) throws Exception {

    	// first, run the query
    	NumericField schemaField = (NumericField) ((GenericComponent)model).getContent().get("schema");
    	int schemaId = schemaField.getNumericValues().get(0).intValue();
    	NumericField maxResultsField = (NumericField) ((GenericComponent)model).getContent().get("schema");
    	int max = maxResultsField.getNumericValues().get(0).intValue();
    	Query query = new Query();
    	Criteria c = new ItemSchemaCriteria(schemaId);
    	query.setCriteria(c);
    	query.addLimitFilter(new LimitFilter(max*3));
    	String[] ids = query.executeQuery();
    	
    	List<GenericComponent> queryModel = new ArrayList<GenericComponent>();
    	int counter = 0;
    	for (String id : ids) {
    		logger.debug("found uri " + id);
    		try {
	    		GenericComponent comp = (GenericComponent) this.getComponentFactory().getComponent(id);
	    		queryModel.add(comp);    		
	    		if (++counter > max)
	    			break;
    		} catch (ItemNotFoundException e) {
    			logger.warn("found component without valid CP: " + id);
    		}
    	}
    	
    	
        // retrieve dispatcher to component JSP view
        // RequestDispatcher dispatcher =
        // config.getServletContext().getRequestDispatcher("/component/JSP/"+ViewID+".jsp");
        RequestDispatcher dispatcher =
                req.getSession()
                        .getServletContext()
                        .getRequestDispatcher(
                                "/components/jsp/" + ViewID + ".jsp");

        // create wrapper (acts as response, but stores output in a
        // CharArrayWriter)
        CharResponseWrapper wrapper = new CharResponseWrapper(res);
        try {
            req.setAttribute("Component", model);
            req.setAttribute("Results", queryModel);
            // run the include
            dispatcher.include(req, wrapper);
        } catch (Exception ex) {
            logger.error("error including",ex);
            return ex.getMessage();
        }

        // and return the wrapper
        return wrapper.toString();
    }

	public void setComponentFactory(ComponentFactory componentFactory) {
		this.componentFactory = componentFactory;
	}

	public ComponentFactory getComponentFactory() {
		return componentFactory;
	}

    @Override
    public boolean canHandleView(String view, HttpServletRequest req,
            HttpServletResponse res) {

        // TODO Auto-generated method stub
        return false;
    }
}

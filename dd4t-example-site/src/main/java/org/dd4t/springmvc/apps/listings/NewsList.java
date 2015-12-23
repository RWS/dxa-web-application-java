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

package org.dd4t.springmvc.apps.listings;

import com.tridion.broker.StorageException;
import com.tridion.broker.querying.CriteriaFactory;
import com.tridion.broker.querying.Query;
import com.tridion.broker.querying.criteria.Criteria;
import com.tridion.broker.querying.criteria.content.ItemSchemaCriteria;
import com.tridion.broker.querying.criteria.content.ItemTypeCriteria;
import com.tridion.broker.querying.criteria.content.PublicationCriteria;
import com.tridion.broker.querying.sorting.SortParameter;
import com.tridion.util.TCMURI;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.springmvc.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class NewsList extends AbstractController {

    public static final String NEWSLIST_COMPS_KEY = "news_list_comps_key";
    private static final Logger LOG = LoggerFactory.getLogger(NewsList.class);
    
    private int newsSchema;
    
    public int getNewsSchema() {
		return newsSchema;
	}

	public void setNewsSchema(int newsSchema) {
		this.newsSchema = newsSchema;
	}

	private ComponentPresentationFactory componentPresentationFactory;
    
	public ComponentPresentationFactory getComponentPresentationFactory() {
		return componentPresentationFactory;
	}

	public void setComponentPresentationFactory(
			ComponentPresentationFactory genericComponentFactory) {
		this.componentPresentationFactory = genericComponentFactory;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		if(LOG.isDebugEnabled()){
			LOG.debug("Entering NewsList");
		}
		
		ModelAndView mav = new ModelAndView("newslist");

		
        try {
            // define a criteria on schema number 254 (newsitem)
            ItemSchemaCriteria isNewsItem = new ItemSchemaCriteria(newsSchema);

            // Only return items of type 16 (components)
            ItemTypeCriteria isComponent = new ItemTypeCriteria(16);

            // get publicationid from the page, and filter query on that too
            Page page =
                    (Page) request
                            .getAttribute(Constants.PAGE_MODEL_KEY);
            TCMURI uri = new TCMURI(page.getId());

            PublicationCriteria isPublication =
                    new PublicationCriteria(uri.getPublicationId());

            // Now, lets concatenate the schema is component criteria
            Criteria itemCritery = CriteriaFactory.And(isNewsItem, isComponent);
            Criteria itemAndPublicationCriteria =
                    CriteriaFactory.And(itemCritery, isPublication);

            // Add these criteria to a query
            Query query = new Query();
            query.setCriteria(itemAndPublicationCriteria);

            // Limit the results to max 5, and sort them ascending on
            // publication date. all available sorting options are constants on
            // the SortParameter class
          //  query.addLimitFilter(new LimitFilter(5));
            SortParameter sortParameter =
                    new SortParameter(
                            SortParameter.ITEMS_INITIAL_PUBLICATION_DATE,
                            SortParameter.ASCENDING);
            query.addSorting(sortParameter);

            // Run the query
            String[] itemResults;
            try {
                itemResults = query.executeQuery();

                List<ComponentPresentation> comps = new ArrayList<ComponentPresentation>();

                // parse array to List of components
                for (String result : itemResults) {
                    
                    /*
                     * Load the components from the factory. Note that we're using getEmbeddedComponent
                     * as we know that in the example these components are found embedded on the page.
                     */
                    ComponentPresentation comp = componentPresentationFactory.getComponentPresentation(result, null);
                    
                    if(comp != null){
                    	comps.add(comp);
                    }                    	
                }
                
                mav.addObject(NEWSLIST_COMPS_KEY, comps);

            } catch (StorageException se) {
                LOG.error("Error while RUNNING query: " + se.getMessage(),
                        se);
            } catch (Exception ex) {
                LOG.error("Error while PARSING query: " + ex.getMessage(),
                        ex);
            }
        } catch (Exception ex) {
            LOG.error("Error while BUILDING query: " + ex.getMessage(), ex);
        }
		
		return mav;
	}

}

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
package com.sdl.tridion.referenceimpl.view.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class acts as main model to contain the rendered component views. It stores views in two indexes:
 * 1. regions. This is a string lookup from view key to a list of rendered HTML strings, stored inside of a ViewRegion object.
 * 2. renderedComponents. This is an ordered list of pointers to sets of html, viewkey and original component presentation stored
 *          inside of a RenderedComponent object.
 * 
 * @author Rogier Oudshoorn
 *
 */
public class ComponentViews {
    private Map<String, ViewRegion> regions;
    private Map<String, List<RenderedComponent>> renderedComponents;

    public ComponentViews() {    	
        this.regions = new HashMap<String, ViewRegion>();
        this.setRenderedComponents(new HashMap<String, List<RenderedComponent>>());
    }

    public Map<String, ViewRegion> getRegions() {
        return regions;
    }

    public void setRegions(Map<String, ViewRegion> regions) {
        this.regions = regions;
    }

    /**
     * The rendered components of a page mapped by region.
     * The original relative order of components within the same region is preserved.
     * 
     * @return
     */
	public Map<String, List<RenderedComponent>> getRenderedComponents() {
		return renderedComponents;
	}

	public void setRenderedComponents(Map<String, List<RenderedComponent>> renderedComponents) {
		this.renderedComponents = renderedComponents;
	}

	/**
	 * Get the list of rendered components for the specified region.
	 * 
	 * @param region
	 * @return
	 */
	public List<RenderedComponent> getRenderedComponents(String region) {
		List<RenderedComponent> regionList = renderedComponents.get(region);
		if (regionList == null) {
			regionList = new ArrayList<RenderedComponent>();
			renderedComponents.put(region, regionList);
		}
		return regionList;
	}
	
	/**
	 * Add the renderedComponent to the list of rendered components for the specified region.
	 * 
	 * @param region
	 * @param renderedComponent
	 */
	public void addRenderedComponent(String region, RenderedComponent renderedComponent) {
		getRenderedComponents(region).add(renderedComponent);
	}

	/**
	 * Sets the map of rendered components.
	 * In addition, the regions map is set based on the HTML strings of the rendered components.
	 * 
	 * @param renderedComponents
	 */
	public void populateFromRenderedComponents(Map<String, List<RenderedComponent>> renderedComponents) {
		setRenderedComponents(renderedComponents);
		Map<String, ViewRegion> newRegions = new HashMap<String, ViewRegion>();
		for (Entry<String, List<RenderedComponent>> region : renderedComponents.entrySet()) {
			ViewRegion viewRegion = new ViewRegion();
			for (RenderedComponent rComp : region.getValue()) {
				viewRegion.getComponentViews().add(rComp.getHtml());
			}
			newRegions.put(region.getKey(), viewRegion);
		}
		setRegions(newRegions);
	}

}
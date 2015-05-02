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
package org.dd4t.springmvc.view.model;

import org.dd4t.contentmodel.ComponentPresentation;

/**
 * This class acts a model for a rendered component. It contains pointers to the rendered html, the region
 * the HTML is supposed to sit in, the original DD4T component presentation that it is based on,
 * and the order of the DD4T component presentation on the page.
 *
 * @author Rogier Oudshoorn
 */
public class RenderedComponent {
	private String html;
	private String region;
	private ComponentPresentation cp;
	private int order;
	
	public RenderedComponent(String html, String region, ComponentPresentation cp, int order){
		this.setHtml(html);
		this.setRegion(region);
		this.setCp(cp);
		this.setOrder(order);
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public ComponentPresentation getCp() {
		return cp;
	}

	public void setCp(ComponentPresentation cp) {
		this.cp = cp;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	/**
	 * When rendered components have been processed they are replaced by a {@link PostProcessedRenderedComponent}.
	 * 
	 * @return
	 */
	public boolean isPostProcessed() {
		return false;
	}

}
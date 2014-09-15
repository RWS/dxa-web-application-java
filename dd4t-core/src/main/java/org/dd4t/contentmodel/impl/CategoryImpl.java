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
package org.dd4t.contentmodel.impl;

import java.util.LinkedList;
import java.util.List;

import org.dd4t.contentmodel.Category;
import org.dd4t.contentmodel.Keyword;
import org.simpleframework.xml.ElementList;


public class CategoryImpl extends BaseItem implements Category {


	@ElementList(name = "keywords", required = false, type = KeywordImpl.class)
	private List<KeywordImpl> keywords;
	
	@Override
	public List<Keyword> getKeywords() {
		List<Keyword> l = new LinkedList<Keyword>();
		for (Keyword k : keywords) {
			l.add(k);
		}
		return l;
	}

	@Override
	public void setKeywords(List<Keyword> keywords) {
		List<KeywordImpl> l = new LinkedList<KeywordImpl>();
		for (Keyword k : keywords) {
			l.add((KeywordImpl)k);
		}
		this.keywords = l;
	}	
}

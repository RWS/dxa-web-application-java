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
package org.dd4t.core.filters.impl;

import org.dd4t.core.filters.Filter;

public abstract class BaseFilter implements Filter {

	private boolean cachingAllowed = true;
	public enum RunPhase { BeforeCaching, AfterCaching, Both };

	@Override
	public boolean getCachingAllowed() {
		return this.cachingAllowed;
	}

	@Override
	public void setCachingAllowed(boolean cachingAllowed) {
		this.cachingAllowed = cachingAllowed;
	}

}

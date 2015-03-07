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
package org.dd4t.core.request.impl;

import org.dd4t.core.request.AbstractRequestContext;
import org.dd4t.core.request.RequestContext;

import javax.servlet.http.HttpServletRequest;


/**
 * Simple implementation of the RequestContext interface.
 * TODO: actually should be renamed to something like HttpServletRequestContext
 * TODO: Move back into the core
 * @author rooudsho
 *
 */
@Deprecated
public class BasicRequestContext extends AbstractRequestContext implements RequestContext {
	private HttpServletRequest req;
	
	public BasicRequestContext(Object request){
		this.req = (HttpServletRequest)request;
	}

	@Override
	public HttpServletRequest getServletRequest() {
		return req;
	}

	// TODO: @Rogier: slight difference to be able to have more than HttpServletRequest
	public boolean isUserInRole(String role) {
		return req.isUserInRole(role);
	}
}

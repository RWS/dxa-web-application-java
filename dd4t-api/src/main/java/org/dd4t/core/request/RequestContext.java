/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
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

package org.dd4t.core.request;

/**
 * TODO: update comment - CWA is sooo old ;)
 * Bean Interface describes the request which is given to CWA for retrieval. It will at the
 * minimum contain a reference to the actual servletrequest (with references to request parameters,
 * headers, etc), but is here mostly to be expanded in implementations.
 * <p/>
 * For instance, adding an authenticated User doing the request would be useful when determining
 * if said user has access to secure content - proving the security filter with whatever input
 * required to match authorisation to authentication supplied by a controller.
 *
 * @author rooudsho
 */

public interface RequestContext {
    Object getRequest ();
}

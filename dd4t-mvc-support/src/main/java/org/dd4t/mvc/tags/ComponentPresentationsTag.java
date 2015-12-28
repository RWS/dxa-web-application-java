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

package org.dd4t.mvc.tags;

/**
 * dd4t-2
 * <p/>
 * NOTE: these tags are defined in dd4t.tld and are copied into the
 * META-INF directory in the jar for dd4t-mvc-support, so you won't have to register this
 * in your own web app.
 *
 * @author R. Kempees
 */

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;

import java.util.List;

/**
 * ComponentPresentationsTag
 *
 * @author R. Kempees, Q. Slings
 */
public class ComponentPresentationsTag extends BaseComponentPresentationsTag {
    @Override
    protected List<ComponentPresentation> getComponentPresentations (Page page) {
        return page.getComponentPresentations();
    }

}
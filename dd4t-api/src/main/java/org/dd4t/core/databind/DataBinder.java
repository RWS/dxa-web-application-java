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

package org.dd4t.core.databind;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Page;
import org.dd4t.core.exceptions.SerializationException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * test
 *
 * @author R. Kempees
 * @since 01/12/14.
 */
public interface DataBinder {

    <T extends Page> T buildPage (final String source, final Class<T> aClass) throws SerializationException;

    <T extends ComponentPresentation> T buildComponentPresentation (final String source, final Class<T> componentPresentationClass) throws SerializationException;

    Map<String, BaseViewModel> buildModels (final Object source, final Set<String> modelNames, final String templateUri) throws SerializationException;

    <T extends BaseViewModel> T buildModel (final Object rawData, final String modelName, final String templateUri) throws SerializationException;

    <T extends BaseViewModel> T buildModel (final Object source, final Class modelClass, final String templateUri) throws SerializationException;

    @Deprecated
    ComponentPresentation buildDynamicComponentPresentation (final ComponentPresentation componentPresentation, final Class<? extends Component> aClass) throws SerializationException;

    <T extends Component> T buildComponent (final Object source, final Class<T> aClass) throws SerializationException;

    String findComponentTemplateViewName (ComponentTemplate template) throws IOException;

    /*
     * Object should be cast to whatever the implementation has as raw
     * deserialization object. For Jackson this is JsonNode
     */
    String getRootElementName (Object componentNode);

    boolean renderDefaultComponentModelsOnly ();

    boolean renderDefaultComponentsIfNoModelFound ();
}

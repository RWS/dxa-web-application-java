/*
 * Copyright (c) 2015 Radagio
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

package org.dd4t.databind;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Page;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.core.databind.DataBinder;
import org.dd4t.core.exceptions.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Singleton entry point for all DD4T classes
 *
 * @author R. Kempees
 */
public class DataBindFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DataBindFactory.class);
    private static final DataBindFactory INSTANCE = new DataBindFactory();

    private DataBinder dataBinder;

    private DataBindFactory () {
        LOG.info("DataBindFactory init.");
    }

    public static DataBindFactory getInstance () {
        if (null == INSTANCE) {
            LOG.error("DataBindFactory not properly instantiated!");
        }
        return INSTANCE;
    }

    public static <T extends Page> T buildPage (final String source, final Class<T> aClass) throws SerializationException {
        return INSTANCE.dataBinder.buildPage(source, aClass);
    }

    public static <T extends ComponentPresentation> T buildDynamicComponentPresentation (final String source, final Class<T> aClass) throws SerializationException {
        return INSTANCE.dataBinder.buildComponentPresentation(source, aClass);
    }

    @Deprecated
    public static ComponentPresentation buildDynamicComponentPresentation (final ComponentPresentation componentPresentation, final Class<? extends Component> aClass) throws SerializationException {
        return INSTANCE.dataBinder.buildDynamicComponentPresentation(componentPresentation, aClass);
    }

    public static <T extends Component> T buildComponent (final Object source, final Class<T> aClass) throws SerializationException {
        return INSTANCE.dataBinder.buildComponent(source, aClass);
    }

    public static Map<String, BaseViewModel> buildModels (final Object rawData, final Set<String> modelNames, final String currentTemplateUri) throws SerializationException {
        return INSTANCE.dataBinder.buildModels(rawData, modelNames, currentTemplateUri);
    }

    public static <T extends BaseViewModel> T buildModel (final Object rawData, final Class<T> model, final String currentTemplateUri) throws SerializationException {
        return INSTANCE.dataBinder.buildModel(rawData, model, currentTemplateUri);
    }

    public static <T extends BaseViewModel> T buildModel (final Object rawData, final String modelName, final String currentTemplateUri) throws SerializationException {
        return INSTANCE.dataBinder.buildModel(rawData, modelName, currentTemplateUri);
    }

    public static String findComponentTemplateViewName (ComponentTemplate template) {
        try {
            return INSTANCE.dataBinder.findComponentTemplateViewName(template);
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public static String getRootElementName (Object componentNode) {
        return INSTANCE.dataBinder.getRootElementName(componentNode);
    }

    public static boolean renderGenericComponentsOnly () {
        return INSTANCE.dataBinder.renderDefaultComponentModelsOnly();
    }

    public static boolean renderDefaultComponentsIfNoModelFound () {
        return INSTANCE.dataBinder.renderDefaultComponentsIfNoModelFound();
    }

    public void setDataBinder (DataBinder dataBinder) {
        this.dataBinder = dataBinder;
    }
}

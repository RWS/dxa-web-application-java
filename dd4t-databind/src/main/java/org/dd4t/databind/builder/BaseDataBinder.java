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

package org.dd4t.databind.builder;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.core.databind.ModelConverter;
import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.util.DataBindConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Base class serving as entry point for all ModelBuilders
 * <p/>
 * - Loads configs
 * - Preloads model classes
 * - Gives a unified / single design pattern to load concrete ModelConverters
 * <p/>
 * TODO: Build an XML dataConverter!
 *
 * @author R. Kempees
 * @since 17/11/14.
 */
public abstract class BaseDataBinder {
    private static final Logger LOG = LoggerFactory.getLogger(BaseDataBinder.class);
    protected static final ConcurrentMap<String, Class<? extends BaseViewModel>> VIEW_MODELS = new ConcurrentHashMap<>();

    protected ModelConverter converter;
    protected String viewModelMetaKeyName;
    protected String viewModelPackageRoot;
    protected boolean renderDefaultComponentModelsOnly;
    protected boolean renderDefaultComponentsIfNoModelFound;
    protected Class<? extends ComponentPresentation> concreteComponentPresentationImpl;
    protected Class<? extends ComponentTemplate> concreteComponentTemplateImpl;
    protected Class<? extends Component> concreteComponentImpl;
    protected Class<? extends Field> concreteFieldImpl;

    public void setConcreteComponentTemplateImpl (final Class<? extends ComponentTemplate> concreteComponentTemplateImpl) {
        this.concreteComponentTemplateImpl = concreteComponentTemplateImpl;
    }

    public void setConcreteComponentPresentationImpl (final Class<? extends ComponentPresentation> concreteComponentPresentationImpl) {
        this.concreteComponentPresentationImpl = concreteComponentPresentationImpl;
    }

    public void setConcreteComponentImpl (final Class<? extends Component> concreteComponentImpl) {
        this.concreteComponentImpl = concreteComponentImpl;
    }

    public void setConverter (final ModelConverter converter) {
        this.converter = converter;
    }

    public void setViewModelMetaKeyName (final String viewModelMetaKeyName) {
        this.viewModelMetaKeyName = viewModelMetaKeyName;
    }

    public void setViewModelPackageRoot (final String viewModelPackageRoot) {
        this.viewModelPackageRoot = viewModelPackageRoot;
    }

    public void setRenderDefaultComponentModelsOnly (final boolean renderDefaultComponentModelsOnly) {
        this.renderDefaultComponentModelsOnly = renderDefaultComponentModelsOnly;
    }

    public void setRenderDefaultComponentsIfNoModelFound (final boolean renderDefaultComponentsIfNoModelFound) {
        this.renderDefaultComponentsIfNoModelFound = renderDefaultComponentsIfNoModelFound;
    }

    public boolean isRenderDefaultComponentModelsOnly () {
        return renderDefaultComponentModelsOnly;
    }

    public boolean isRenderDefaultComponentsIfNoModelFound () {
        return renderDefaultComponentsIfNoModelFound;
    }

    public Class<? extends ComponentPresentation> getConcreteComponentPresentationImpl () {
        return concreteComponentPresentationImpl;
    }

    public Class<? extends ComponentTemplate> getConcreteComponentTemplateImpl () {
        return concreteComponentTemplateImpl;
    }

    public Class<? extends Component> getConcreteComponentImpl () {
        return concreteComponentImpl;
    }

    public void setConcreteFieldImpl (final Class<? extends Field> concreteFieldImpl) {
        this.concreteFieldImpl = concreteFieldImpl;
    }

    public boolean renderDefaultComponentModelsOnly () {
        return renderDefaultComponentModelsOnly;
    }

    public boolean renderDefaultComponentsIfNoModelFound () {
        return renderDefaultComponentsIfNoModelFound;
    }

    public ModelConverter getConverter () {
        return converter;
    }

    public String getViewModelMetaKeyName () {
        return viewModelMetaKeyName;
    }

    public String getViewModelPackageRoot () {
        return viewModelPackageRoot;
    }

    public Class<? extends Field> getConcreteFieldImpl () {
        return concreteFieldImpl;
    }

    protected static BaseViewModel getModelOrNullForExistingEntry (Map<String, BaseViewModel> models, Class modelClass) {
        for (BaseViewModel baseViewModel : models.values()) {
            LOG.debug(baseViewModel.getClass().getName() + "==" + modelClass.getName());
            if (baseViewModel.getClass().equals(modelClass)) {
                return baseViewModel;
            }
        }
        return null;
    }

    @PostConstruct
    protected abstract void init ();

    protected void checkViewModelConfiguration () {
        if (StringUtils.isEmpty(viewModelMetaKeyName)) {
            this.viewModelMetaKeyName = DataBindConstants.VIEW_MODEL_DEFAULT_META_KEY;
            LOG.warn("Setting meta key to default: " + DataBindConstants.VIEW_MODEL_DEFAULT_META_KEY);
        }

        if (StringUtils.isEmpty(viewModelPackageRoot)) {
            this.viewModelPackageRoot = DataBindConstants.VIEW_MODEL_DEFAULT_NAMESPACE;
            LOG.warn("No package root configured for view models. Using the default package: " + DataBindConstants.VIEW_MODEL_DEFAULT_NAMESPACE);
        }

        LOG.info("View model key name is: " + this.viewModelMetaKeyName);
        LOG.info("Root package for View models is: " + this.viewModelPackageRoot);
    }

    protected void scanAndLoadModels () {
        LOG.info("Init: scanning view models.");
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ViewModel.class));
        for (BeanDefinition bd : scanner.findCandidateComponents(this.viewModelPackageRoot)) {
            try {
                final Class clazz = Class.forName(bd.getBeanClassName());
                if (clazz != null) {
                    LOG.debug("Loading class: " + clazz.getCanonicalName());
                    final ViewModel viewModelParameters = (ViewModel) clazz.getAnnotation(ViewModel.class);

                    if (hasProperModelInformation(viewModelParameters, clazz)) {
                        LOG.debug("Parameters: Viewmodel name(s):{}, Root Element name(s){}, Set Component: {}, Set raw: {} ", new Object[]{viewModelParameters.viewModelNames(), viewModelParameters.rootElementNames(), viewModelParameters.setComponentObject(), viewModelParameters.setRawData()});
                        storeModelClass(viewModelParameters, clazz);
                    }
                }
                LOG.info("Init: Done scanning view models.");
            } catch (ClassNotFoundException e) {
                LOG.error("Unexpected exception", e);
            }
        }
    }

    private boolean hasProperModelInformation (ViewModel viewModelParameters, Class clazz) {

        if (viewModelParameters.rootElementNames().length == 0 && viewModelParameters.viewModelNames().length == 0 && !viewModelParameters.setComponentObject()) {
            LOG.warn("No viewmodel and rootelement name configuration set and SetComponentObject is false. Not using this model: {}", clazz.getCanonicalName());
            return false;
        }

        if (StringUtils.isEmpty(viewModelParameters.rootElementNames()[0]) && StringUtils.isEmpty(viewModelParameters.viewModelNames()[0]) && !viewModelParameters.setComponentObject()) {
            LOG.warn("No viewmodel and rootelement name configuration set and SetComponentObject is false. Not using this model: {}", clazz.getCanonicalName());
            return false;
        }

        return true;
    }

    /**
     * Get the key with which a model class is registered, which is either
     * related models.
     * <p/>
     * We should be able to get multiple keys for the same view model
     * <p/>
     * We should be able to match both view models and schema root element names. So the
     */
    private static void storeModelClass (final ViewModel viewModelParameters, final Class model) {

        final List<String> modelNames = new ArrayList<>();
        String[] viewModelNames = viewModelParameters.viewModelNames();

        if (null != viewModelNames && viewModelNames.length > 0) {
            modelNames.addAll(Arrays.asList(viewModelNames));

        }
        String[] rootElementNames = viewModelParameters.rootElementNames();
        if (null != rootElementNames && rootElementNames.length > 0) {
            modelNames.addAll(Arrays.asList(rootElementNames));
        }
        storeModelClassForModelNames(modelNames, model);
    }

    private static void storeModelClassForModelNames (final List<String> viewModelNames, final Class model) {
        for (String viewModelName : viewModelNames) {
            LOG.info("Storing viewModelName: {}, for class: {}", viewModelName, model.toString());
            if (VIEW_MODELS.containsKey(viewModelName)) {
                LOG.warn("Key: {} already exists! Model for key is: {}", viewModelName, model.toString());
            } else {
                VIEW_MODELS.put(viewModelName, model);
            }
        }
    }
}

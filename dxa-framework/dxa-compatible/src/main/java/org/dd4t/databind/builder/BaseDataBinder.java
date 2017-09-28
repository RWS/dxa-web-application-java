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

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ClassInfo;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
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
 *
 * @author R. Kempees
 * @since 17/11/14.
 */
public abstract class BaseDataBinder {
    protected static final ConcurrentMap<String, List<Class<? extends BaseViewModel>>> VIEW_MODELS = new ConcurrentHashMap<>();

    protected static final ConcurrentMap<String, List<Class<? extends BaseViewModel>>> ABSTRACT_OR_INTERFACE_MODELS = new ConcurrentHashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(BaseDataBinder.class);

    protected ModelConverter converter;
    protected String viewModelMetaKeyName;
    protected String viewModelPackageRoot;
    protected boolean renderDefaultComponentModelsOnly;
    protected boolean renderDefaultComponentsIfNoModelFound;
    protected Class<? extends ComponentPresentation> concreteComponentPresentationImpl;
    protected Class<? extends ComponentTemplate> concreteComponentTemplateImpl;
    protected Class<? extends Component> concreteComponentImpl;
    protected Class<? extends Field> concreteFieldImpl;

    protected static BaseViewModel getModelOrNullForExistingEntry(Map<String, BaseViewModel> models, Class modelClass) {
        for (BaseViewModel baseViewModel : models.values()) {
            LOG.debug(baseViewModel.getClass().getName() + "==" + modelClass.getName());
            if (baseViewModel.getClass().equals(modelClass)) {
                return baseViewModel;
            }
        }
        return null;
    }

    private static void addAbstractOrInterfaceToWatchList(final ClassInfo classInfo, final Class<? extends BaseViewModel> concreteModelClass) {
        final List<Class<? extends BaseViewModel>> concreteClasses;
        if (ABSTRACT_OR_INTERFACE_MODELS.containsKey(classInfo.getClassName())) {
            concreteClasses = ABSTRACT_OR_INTERFACE_MODELS.get(classInfo.getClassName());


            if (!concreteClasses.contains(concreteModelClass)) {
                concreteClasses.add(concreteModelClass);
            }
        } else {
            concreteClasses = new ArrayList<>();
            concreteClasses.add(concreteModelClass);
            ABSTRACT_OR_INTERFACE_MODELS.putIfAbsent(classInfo.getClassName(), concreteClasses);
        }
    }

    /**
     * Get the key with which a model class is registered, which is either
     * related models.
     * <p/>
     * We should be able to get multiple keys for the same view model
     * <p/>
     * We should be able to match both view models and schema root element names. So the
     */
    private static void storeModelClass(final ViewModel viewModelParameters, final Class<? extends BaseViewModel> model) {

        final List<String> modelNames = new ArrayList<>();
        String[] viewModelNames = viewModelParameters.viewModelNames();

        if (viewModelNames.length > 0) {
            modelNames.addAll(Arrays.asList(viewModelNames));

        }
        String[] rootElementNames = viewModelParameters.rootElementNames();
        if (rootElementNames.length > 0) {
            modelNames.addAll(Arrays.asList(rootElementNames));
        }
        storeModelClassForModelNames(modelNames, model);
    }

    private static void storeModelClassForModelNames(final List<String> viewModelNames, final Class<? extends BaseViewModel> model) {
        for (String viewModelName : viewModelNames) {
            LOG.info("Storing viewModelName: {}, for class: {}", viewModelName, model.toString());


            if (StringUtils.isNotEmpty(viewModelName)) {

                List<Class<? extends BaseViewModel>> classList = VIEW_MODELS.get(viewModelName);
                if (classList == null) {
                    classList = new ArrayList<>();
                    classList.add(model);

                    // Store different classes for the same key
                    // Determine which class to use at deserialization time?
                    // No, just deserialize all.


                    VIEW_MODELS.putIfAbsent(viewModelName, classList);


                } else {
                    LOG.info("Key: {} already exists. Model for key is: {}", viewModelName, model.toString());

                    if (!classList.contains(model)) {
                        classList.add(model);
                    }
                }
            }
        }
    }

    public boolean isRenderDefaultComponentModelsOnly () {
        return renderDefaultComponentModelsOnly;
    }

    public void setRenderDefaultComponentModelsOnly(final boolean renderDefaultComponentModelsOnly) {
        this.renderDefaultComponentModelsOnly = renderDefaultComponentModelsOnly;
    }

    public boolean isRenderDefaultComponentsIfNoModelFound () {
        return renderDefaultComponentsIfNoModelFound;
    }

    public void setRenderDefaultComponentsIfNoModelFound(final boolean renderDefaultComponentsIfNoModelFound) {
        this.renderDefaultComponentsIfNoModelFound = renderDefaultComponentsIfNoModelFound;
    }

    public Class<? extends ComponentPresentation> getConcreteComponentPresentationImpl () {
        return concreteComponentPresentationImpl;
    }

    public void setConcreteComponentPresentationImpl(final Class<? extends ComponentPresentation> concreteComponentPresentationImpl) {
        this.concreteComponentPresentationImpl = concreteComponentPresentationImpl;
    }

    public Class<? extends ComponentTemplate> getConcreteComponentTemplateImpl () {
        return concreteComponentTemplateImpl;
    }

    public void setConcreteComponentTemplateImpl(final Class<? extends ComponentTemplate> concreteComponentTemplateImpl) {
        this.concreteComponentTemplateImpl = concreteComponentTemplateImpl;
    }

    public Class<? extends Component> getConcreteComponentImpl () {
        return concreteComponentImpl;
    }

    public void setConcreteComponentImpl(final Class<? extends Component> concreteComponentImpl) {
        this.concreteComponentImpl = concreteComponentImpl;
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

    public void setConverter(final ModelConverter converter) {
        this.converter = converter;
    }

    public String getViewModelMetaKeyName () {
        return viewModelMetaKeyName;
    }

    public void setViewModelMetaKeyName(final String viewModelMetaKeyName) {
        this.viewModelMetaKeyName = viewModelMetaKeyName;
    }

    public String getViewModelPackageRoot () {
        return viewModelPackageRoot;
    }

    public void setViewModelPackageRoot(final String viewModelPackageRoot) {
        this.viewModelPackageRoot = viewModelPackageRoot;
    }

    public Class<? extends Field> getConcreteFieldImpl () {
        return concreteFieldImpl;
    }

    public void setConcreteFieldImpl(final Class<? extends Field> concreteFieldImpl) {
        this.concreteFieldImpl = concreteFieldImpl;
    }

    public boolean classHasViewModelDerivatives(String className) {
        return ABSTRACT_OR_INTERFACE_MODELS.containsKey(className);
    }

    public Class<? extends BaseViewModel> getConcreteModel(final String className, final String rootElementName) {
        // Check if we have a model class for the root element name
        // Check if it matches the interface or abstract class

        if (VIEW_MODELS.containsKey(rootElementName)) {
            final List<Class<? extends BaseViewModel>> modelClasses = VIEW_MODELS.get(rootElementName);

            if (modelClasses == null || modelClasses.isEmpty()) {
                return null;
            }

            if (ABSTRACT_OR_INTERFACE_MODELS.containsKey(className)) {
                final List<Class<? extends BaseViewModel>> concreteClasses = ABSTRACT_OR_INTERFACE_MODELS.get(className);


                for (Class<? extends BaseViewModel> clazz : concreteClasses) {
                    for (Class<? extends BaseViewModel> modelClass : modelClasses) {
                        LOG.debug(clazz.getClass().getName() + "==" + modelClass.getName());
                        if (modelClass.equals(clazz)) {
                            return clazz;
                        }
                    }
                }

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

        final FastClasspathScanner scanner = new FastClasspathScanner(this.viewModelPackageRoot);
        final ScanResult scanResult = scanner.scan();

        for (Map.Entry<String, ClassInfo> classInfoEntry : scanResult.getClassNameToClassInfo().entrySet()) {
            LOG.info(classInfoEntry.getValue().getClassName());
            processClass(classInfoEntry.getValue());
        }
        LOG.info("Init: Done scanning view models.");
    }

    private void processClass (final ClassInfo classInfo) {

        LOG.debug("Name: {}, Has abstracts or interfaces: {}", classInfo.getClassName(), classInfo.getDirectlyImplementedInterfaces().size() > 0 || hasNonStandardParent(classInfo));

        if (isDatabindStandardClass(classInfo) || classInfo.getClassName().equals(Object.class.getCanonicalName())) {
            LOG.debug("Not processing standard class: " + classInfo.getClassName());
            return;
        }

        try {

            if (classInfo.hasAnnotation(DataBindConstants.VIEW_MODEL_ANNOTATION_NAME)) {
                processViewModelClass(classInfo);
            } else if (classInfo.isImplementedInterface()) {
                processInterfaceForModels(classInfo);
            }

            if (classInfo.getSubclasses() != null && classInfo.getSubclasses().size() > 0) {
                LOG.debug("we have subclasses. processing.");


                for (ClassInfo subClassInfo : classInfo.getSubclasses()) {

                    final Class<? extends BaseViewModel> concreteModelClass = (Class<? extends BaseViewModel>) Class.forName(subClassInfo.getClassName());
                    addAbstractOrInterfaceToWatchList(classInfo,concreteModelClass);
                    processClass(subClassInfo);
                }
            }
        } catch (ClassNotFoundException e) {
            LOG.error("Unexpected exception", e);
        }
    }

    private void processInterfaceForModels (final ClassInfo classInfo) throws ClassNotFoundException {
        LOG.debug("This is an interface. Searching for classes which are ViewModels which implement this interface");

        for (ClassInfo implementingClass : classInfo.getClassesImplementing()) {
            LOG.debug(implementingClass.getClassName() + " implements interface: " + classInfo.getClassName() + ". Making note of it.");

            final Class<? extends BaseViewModel> concreteModelClass = (Class<? extends BaseViewModel>) Class.forName(implementingClass.getClassName());
// TODO: not the most efficient
            addAbstractOrInterfaceToWatchList(classInfo, concreteModelClass);
            processClass(implementingClass);
            LOG.info("Added interface: {}, direct class: {}", classInfo.getClassName(), implementingClass.getClassName());
        }
    }

    private void processViewModelClass (final ClassInfo classInfo) throws ClassNotFoundException {
        final Class<? extends BaseViewModel> clazz = (Class<? extends BaseViewModel>) Class.forName(classInfo.getClassName());

        if (clazz != null) {
            LOG.debug("Loading class: " + clazz.getCanonicalName());


            final ViewModel viewModelParameters = clazz.getAnnotation(ViewModel.class);

            if (hasProperModelInformation(viewModelParameters, clazz)) {
                LOG.debug("Parameters: Viewmodel name(s):{}, Root Element name(s){}, Set Component: {}, Set raw: {} ", viewModelParameters.viewModelNames(), viewModelParameters.rootElementNames(), viewModelParameters.setComponentObject(), viewModelParameters.setRawData());
                storeModelClass(viewModelParameters, clazz);
            }
        }
    }

    private boolean isDatabindStandardClass (ClassInfo classInfo) {
        return classInfo.getClassName().equals(DataBindConstants.VIEW_MODEL_ANNOTATION)
                || classInfo.getClassName().equals(DataBindConstants.VIEW_MODEL_BASE_CLASS_NAME)
                || classInfo.getClassName().equals(DataBindConstants.TRIDION_VIEW_MODEL_BASE_CLASS_NAME)
                || classInfo.getClassName().equals(DataBindConstants.TRIDION_VIEW_MODEL_INTERFACE)
                || classInfo.getClassName().equals(DataBindConstants.BASE_VIEW_MODEL_INTERFACE);
    }

    private boolean hasNonStandardParent (ClassInfo classInfo) {
        final ClassInfo superClass = classInfo.getDirectSuperclass();

        return superClass != null && (!superClass.getClassName().equals(DataBindConstants.VIEW_MODEL_BASE_CLASS_NAME) && !superClass.getClassName().equals(DataBindConstants.TRIDION_VIEW_MODEL_BASE_CLASS_NAME));

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
}
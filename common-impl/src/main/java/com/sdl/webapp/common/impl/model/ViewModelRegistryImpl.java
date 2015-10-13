package com.sdl.webapp.common.impl.model;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sdl.webapp.common.api.mapping.SemanticMapping;
import com.sdl.webapp.common.api.mapping.annotations.*;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.*;
import com.sdl.webapp.common.api.model.page.AbstractPageModelImpl;
import com.sdl.webapp.common.api.model.page.PageModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.SimpleRegionMvcData;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.impl.mapping.MvcDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of {@code ViewModelRegistry}.
 */
@Component
public class ViewModelRegistryImpl implements ViewModelRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(ViewModelRegistryImpl.class);

    private final Map<MvcData, Class<? extends ViewModel>> viewEntityClassMap = new HashMap<>();
    private final Map<Class<? extends ViewModel>, SemanticInfo> modelTypeToSemanticInfoMapping = new HashMap<>();
    private final Map<String, List<Class<? extends ViewModel>>> semanticTypeToModelTypesMapping = new HashMap<>();

    //TODO: Check whether this is really autowired

    private SemanticMapping semanticMapping;
    private Lock lock;

    //TODO : initialize these in the core module
    @Autowired
    public ViewModelRegistryImpl(SemanticMapping semanticMapping) {
        this.semanticMapping = semanticMapping;
        this.lock = new ReentrantLock();

    }

    static private class SemanticInfo {
        final Map<String, String> prefixMappings = new HashMap<>();
        final List<String> publicSemanticTypes = new ArrayList<>();
        final List<String> mappedSemanticTypes = new ArrayList<>();
        final Map<String, List<String>> semanticProperties = new HashMap<>();
    }

    @Override
    public void registerViewModel(MvcData viewData, Class<? extends ViewModel> entityClass) {
        try {
            //TODO: TW put back the lock to 10
            if (lock.tryLock(100, TimeUnit.SECONDS)) {
                if (viewData != null) {
                    if (this.viewEntityClassMap.containsKey(viewData)) {
                        LOG.warn("View % registered multiple times.", viewData);
                        return;
                    }
                    viewEntityClassMap.put(viewData, entityClass);
                }

                if (!modelTypeToSemanticInfoMapping.containsKey(entityClass)) {
                    registerModelType(entityClass);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (DxaException e) {
            e.printStackTrace();
        } finally {
            //release lock
            lock.unlock();
        }
    }


    private SemanticInfo registerModelType(Class<? extends ViewModel> modelType) throws DxaException {
        SemanticInfo semanticInfo = extractSemanticInfo(modelType);
        modelTypeToSemanticInfoMapping.put(modelType, semanticInfo);

        for (String semanticTypeName : semanticInfo.mappedSemanticTypes) {
            List<Class<? extends ViewModel>> mappedModelTypes;
            if (!semanticTypeToModelTypesMapping.containsKey(semanticTypeName)) {
                mappedModelTypes = new ArrayList<>();
                this.semanticTypeToModelTypesMapping.put(semanticTypeName, mappedModelTypes);
            } else {
                mappedModelTypes = semanticTypeToModelTypesMapping.get(semanticTypeName);
            }
            mappedModelTypes.add(modelType);
        }

        if (!semanticInfo.publicSemanticTypes.isEmpty()) {
            LOG.debug("Model type '{}' has semantic type(s) '{}'.", modelType, semanticInfo.publicSemanticTypes); //StringUtils.join(semanticInfo.PublicSemanticTypes, " "));
            for (Map.Entry<String, List<String>> kvp : semanticInfo.semanticProperties.entrySet()) {
                LOG.debug("\tRegistered property '{}' as semantic property '{}'", kvp.getKey(), kvp.getValue()); //StringUtils.join(kvp.getValue(), " "));
            }
        }
        return semanticInfo;
    }

    private void extractSemanticInfoFromAnnotation(SemanticEntity attribute, SemanticInfo semanticInfo) throws DxaException {
        semanticInfo.mappedSemanticTypes.add(semanticMapping.getQualifiedTypeName(attribute.entityName(), attribute.vocabulary()));

        if (!attribute.public_() || attribute.prefix()==null || attribute.prefix().trim().equals(""))
            return;

        String prefix = attribute.prefix();
        String registeredVocab;
        if (semanticInfo.prefixMappings.containsKey(prefix))
        {
            registeredVocab = semanticInfo.prefixMappings.get(prefix);
            // Prefix mapping already exists; must match.
            if (!attribute.vocabulary().equals(registeredVocab))
            {
                throw new DxaException(
                        String.format("Attempt to use semantic prefix '%s' for vocabulary '%s', but is is already used for vocabulary '%s",
                                prefix, attribute.vocabulary(), registeredVocab)
                );
            }
        }
        else
        {
            semanticInfo.prefixMappings.put(prefix, attribute.vocabulary());
        }

        semanticInfo.publicSemanticTypes.add(String.format("%s:%s", prefix, attribute.entityName()));

    }

    private SemanticInfo extractSemanticInfo(Class<? extends ViewModel> modelType) throws DxaException {


        SemanticInfo semanticInfo = new SemanticInfo();

        // Built-in semantic type mapping
        String bareTypeName = modelType.getSimpleName();
        semanticInfo.mappedSemanticTypes.add(semanticMapping.getQualifiedTypeName(bareTypeName, null));

        // Extract semantic info from SemanticEntity attributes on the Model Type.
        if(modelType.isAnnotationPresent(SemanticEntity.class)){
            extractSemanticInfoFromAnnotation(modelType.getAnnotation(SemanticEntity.class), semanticInfo);
        }else if(modelType.getClass().isAnnotationPresent(SemanticEntities.class)){
            SemanticEntities annotations = modelType.getClass().getAnnotation(SemanticEntities.class);
            for (SemanticEntity attribute : annotations.value())
            {
                extractSemanticInfoFromAnnotation(attribute, semanticInfo);
            }
        }

        // Extract semantic info from SemanticEntity attributes on the Model Type's properties
        for (Field field : modelType.getDeclaredFields())
        {
            if(field.isAnnotationPresent(SemanticProperty.class)){
                SemanticProperty prop = field.getAnnotation(SemanticProperty.class);
                if(!skipSemanticProperty(prop, semanticInfo)){
                    updateSemanticInfo(semanticInfo, field.getName(),prop.value());
                }
            }else if(field.isAnnotationPresent(SemanticProperties.class)){
                for(SemanticProperty prop : field.getAnnotation(SemanticProperties.class).value()){
                    updateSemanticInfo(semanticInfo, field.getName(),prop.value());
                }

            }
        }
        return semanticInfo;
    }
    private void updateSemanticInfo(SemanticInfo semanticInfo, String fieldName, String propertyName){
        List<String> semanticPropertyNames = null;
        if (semanticInfo.semanticProperties.containsKey(propertyName))
        {
            semanticPropertyNames = semanticInfo.semanticProperties.get(propertyName);
        }
        if(semanticPropertyNames==null){
            semanticPropertyNames = new ArrayList<String>();
            semanticInfo.semanticProperties.put(fieldName, semanticPropertyNames);
        }
        semanticPropertyNames.add(propertyName);
    }
    private boolean skipSemanticProperty(SemanticProperty attribute, SemanticInfo semanticInfo){
        if (StringUtils.isEmpty(attribute.value()))
        {
            return true;
        }
        String[] semanticPropertyNameParts = attribute.value().split(":");
        if (semanticPropertyNameParts.length < 2)
        {
            return true;
        }
        String prefix = semanticPropertyNameParts[0];
        if (!semanticInfo.prefixMappings.containsKey(prefix))
        {
            return true;
        }
        return false;
    }

    @Override
    public Class<? extends ViewModel> getViewModelType(final MvcData viewData) throws DxaException {
        Class modelType = null;

        Predicate<Map.Entry<MvcData, Class<? extends ViewModel>>> keyNamePredicate =
                new Predicate<Map.Entry<MvcData, Class<? extends ViewModel>>>() {
                    @Override
                    public boolean apply(Map.Entry<MvcData, Class<? extends ViewModel>> input) {
                        MvcData thisKey = input.getKey();
                        return thisKey.getViewName().equals(viewData.getViewName()) &&
                                thisKey.getControllerName().equals(viewData.getControllerName()) &&
                                thisKey.getAreaName().equals(viewData.getAreaName());
                    }
                };
        Predicate<Map.Entry<MvcData, Class<? extends ViewModel>>> keyNamePredicateNoArea =
                new Predicate<Map.Entry<MvcData, Class<? extends ViewModel>>>() {
                    @Override
                    public boolean apply(Map.Entry<MvcData, Class<? extends ViewModel>> input) {
                        MvcData thisKey = input.getKey();
                        return thisKey.getViewName().equals(viewData.getViewName()) &&
                                thisKey.getControllerName().equals(viewData.getControllerName());
                    }
                };
        Map<MvcData, Class<? extends ViewModel>> possibleValues = Maps.filterEntries(this.viewEntityClassMap, keyNamePredicate);
        if (possibleValues.isEmpty()) {
            //first let's see if there is another relevant view
            possibleValues = Maps.filterEntries(this.viewEntityClassMap, keyNamePredicateNoArea);
        }
        if (possibleValues.isEmpty()) {
            throw new DxaException(String.format("Could not find a view model for the view data %s", viewData));
        } else {
            return possibleValues.entrySet().iterator().next().getValue();
        }
    }

    @Override
    public Class<? extends ViewModel> getMappedModelTypes(String semanticTypeName) throws DxaException {
        //TODO: CB, implement this correctly, based on semantics
        //TODO: TW, implemented as per .net code
        //TODO: TW, the semanticTypeName needs to be provided with vobabulary ID, otherwise it won't return any class
        if(semanticTypeToModelTypesMapping.containsKey(semanticTypeName)){
            //TODO: TW, .net returns a list and if found more than one (i.e. list has more than one value, returns the base type
            List<Class<? extends ViewModel>> l = semanticTypeToModelTypesMapping.get(semanticTypeName);
            if(l.size()>=1){
                return l.get(0);
            }
        }
        //TODO: TW, shall we fallback to the old way?
        //TODO: TW Validate we don't need the old Implementation
         MvcData mvcData = new MvcDataImpl(semanticTypeName);
         return getViewModelType(mvcData);
        //return null;
    }

    @Override
    public Class<? extends ViewModel> getViewEntityClass(final String viewName) throws DxaException {

        final String areaName;
        final String scopedViewName;
        if ( !viewName.contains(":") ) { // Core module
            areaName = "Core";
            scopedViewName = viewName;
        }
        else {
            String[] parts = viewName.split(":");
            areaName = parts[0];
            scopedViewName = parts[1];
        }
        Predicate<Map.Entry<MvcData, Class<? extends ViewModel>>> keyNamePredicate =
                new Predicate<Map.Entry<MvcData, Class<? extends ViewModel>>>() {
                    @Override
                    public boolean apply(Map.Entry<MvcData, Class<? extends ViewModel>> input) {
                        return input.getKey().getAreaName().equals(areaName) && input.getKey().getViewName().equals(scopedViewName);
                    }
                };


        Map<MvcData, Class<? extends ViewModel>> possibleValues = Maps.filterEntries(this.viewEntityClassMap, keyNamePredicate);
        if (possibleValues.isEmpty()) {
            throw new DxaException(String.format("Could not find a view model for the view name %s", viewName));
        } else {
            return possibleValues.entrySet().iterator().next().getValue();
        }

    }


    @Override
    public void registerViewEntityClass(String viewName, Class<? extends ViewModel> entityClass) throws DxaException {
        MvcData mvcData = new MvcDataImpl(viewName);
        registerViewModel(mvcData, entityClass);
    }

        /// <summary>
        /// Gets the semantic property names for a given Model Type and property name.
        /// </summary>
        /// <param name="modelType">The Model Type.</param>
        /// <param name="propertyName">The property name.</param>
        /// <returns>The semantic property names or <c>null</c> if no semantic property names have been registered for the given property.</returns>

        public  String[] getSemanticPropertyNames(Class<? extends ViewModel> modelType, String propertyName) throws DxaException {
            // No Tracer here to reduce trace noise.
            SemanticInfo semanticInfo = getSemanticInfo(modelType);

            List<String> semanticPropertyNames;

            return null;
        }

        private SemanticInfo getSemanticInfo(Class<? extends ViewModel> modelType) throws DxaException {
            SemanticInfo semanticInfo = null;
            if (!modelTypeToSemanticInfoMapping.containsKey(modelType))
            {
                // Just-In-Time model type registration.
                semanticInfo = registerModelType(modelType);
            }
            return semanticInfo;
        }


}

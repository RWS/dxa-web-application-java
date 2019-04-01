package com.sdl.webapp.common.impl.model;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.getDefaultAreaName;

@Component
@Slf4j
@ToString
@CacheConfig(cacheNames = "defaultCache", keyGenerator = "localizationAwareKeyGenerator")
public class ViewModelRegistryImpl implements ViewModelRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(ViewModelRegistryImpl.class);

    private static final Map<MvcData, Class<? extends ViewModel>> viewEntityClassMap = new HashMap<>();

    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Autowired
    private SemanticMappingRegistry semanticMappingRegistry;

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable
    public Class<? extends ViewModel> getViewEntityClass(final String viewName) throws DxaException {

        final String areaName;
        final String scopedViewName;
        if (!viewName.contains(":")) { // default module
            areaName = getDefaultAreaName();
            scopedViewName = viewName;
        } else {
            String[] parts = viewName.split(":");
            areaName = parts[0];
            scopedViewName = parts[1];
        }
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        try {
            readLock.lock();
            return viewEntityClassMap.entrySet().stream()
                    .filter(mvcData -> mvcData.getKey().getAreaName().equals(areaName) && mvcData.getKey().getViewName().equals(scopedViewName))
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .orElseThrow(() -> new DxaException(String.format("Could not find a view model for the view name %s", viewName)));
        } finally {
            readLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable
    public Class<? extends ViewModel> getMappedModelTypes(Set<String> semanticTypeNames, @Nullable Class<? extends EntityModel> expectedClass) throws DxaException {
        Class<? extends ViewModel> viewModelType;
        List<Exception> exceptions = new ArrayList<>();
        for (String fullyQualifiedName : semanticTypeNames) {
            try {
                viewModelType = getMappedModelTypes(fullyQualifiedName, expectedClass);
            } catch (IllegalStateException ex) {
                //means mapping not found
                exceptions.add(ex);
                continue;
            }
            if (viewModelType != null) {
                return viewModelType;
            }
        }
        throw new DxaException("Cannot determine view model type for semantic schema names: '" + semanticTypeNames + "'. Please make sure " +
                "that an entry is registered for this view name in the ViewModelRegistry. Collected exceptions: " + exceptions);
    }

    @Override
    @Cacheable
    public Class<? extends ViewModel> getMappedModelTypes(Set<String> semanticTypeNames) {
        try {
            return getMappedModelTypes(semanticTypeNames, null);
        } catch (DxaException e) {
            log.warn("Cannot get entity model type for {}" + semanticTypeNames, e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable
    public Class<? extends ViewModel> getMappedModelTypes(String semanticTypeName, @Nullable Class<? extends EntityModel> expectedClass) throws DxaException {

        Class<? extends ViewModel> retval;
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        try {
            readLock.lock();
            retval = this.semanticMappingRegistry.getEntityClassByFullyQualifiedName(semanticTypeName, expectedClass);
        } catch (SemanticMappingException e) {
            throw new DxaException("Cannot get a view model tpe because of semantic mapping exception", e);
        } finally {
            readLock.unlock();
        }

        if (retval != null) {
            return retval;
        }
        //Fallback
        MvcData mvcData = MvcDataCreator.creator().fromQualifiedName(semanticTypeName).create();
        return getViewModelType(mvcData);
    }

    @Override
    @Cacheable
    public Class<? extends ViewModel> getMappedModelTypes(String semanticTypeName) {
        try {
            return getMappedModelTypes(semanticTypeName, null);
        } catch (DxaException e) {
            log.warn("Cannot get entity model type for " + semanticTypeName, e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable
    public Class<? extends ViewModel> getViewModelType(final MvcData viewData) {
        Set<Map.Entry<MvcData, Class<? extends ViewModel>>> entries = new HashSet<>();
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        try{
            readLock.lock();
            entries.addAll(viewEntityClassMap.entrySet().stream().filter(mvcData -> {
                MvcData key = mvcData.getKey();
                return key.getViewName().equals(viewData.getViewName());
            }).collect(Collectors.toSet()));
        } finally {
            readLock.unlock();
        }
        Class<? extends ViewModel> exactClassForModelData = entries.stream()
                .filter(mvcData -> {
                    MvcData key = mvcData.getKey();
                    return key.getControllerName().equals(viewData.getControllerName()) &&
                           key.getAreaName().equals(viewData.getAreaName());
                })
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

        if (exactClassForModelData != null) {
            return exactClassForModelData;
        }

        Class<? extends ViewModel> probablyClassForModelData = entries.stream()
                .filter(mvcData -> {
                    MvcData key = mvcData.getKey();
                    return key.getControllerName().equals(viewData.getControllerName()) || Strings.isNullOrEmpty(viewData.getControllerName());
                })
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
        if (probablyClassForModelData != null) {
            return probablyClassForModelData;
        }
        throw new IllegalStateException("Cannot detect ViewModel for ViewData " + viewData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerViewModel(MvcData viewData, Class<? extends ViewModel> entityClass) {
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            if (viewData != null) {
                if (viewEntityClassMap.containsKey(viewData)) {
                    LOG.warn("View {} registered multiple times, ignoring.", viewData);
                    return;
                }
                viewEntityClassMap.put(viewData, entityClass);
            }
            semanticMappingRegistry.registerEntity((Class<? extends EntityModel>) entityClass);
        } finally {
            writeLock.unlock();
        }
    }
}
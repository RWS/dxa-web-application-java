package com.sdl.webapp.common.impl.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.MvcDataImpl;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.exceptions.DxaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ViewModelRegistryImpl implements ViewModelRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(ViewModelRegistryImpl.class);

    // todo TSI-1063 should not be static
    private static final Map<MvcData, Class<? extends ViewModel>> viewEntityClassMap = new HashMap<>();

    private static Lock lock = new ReentrantLock();

    @Autowired
    private SemanticMappingRegistry semanticMappingRegistry;

    @Override
    public void registerViewModel(MvcData viewData, Class<? extends ViewModel> entityClass) {
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                if (viewData != null) {
                    if (viewEntityClassMap.containsKey(viewData)) {
                        LOG.warn("View % registered multiple times.", viewData);
                        return;
                    }
                    viewEntityClassMap.put(viewData, entityClass);
                }
                semanticMappingRegistry.registerEntity((Class<? extends EntityModel>) entityClass);
            }
        } catch (InterruptedException e) {
            LOG.warn(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Class<? extends ViewModel> getViewModelType(final MvcData viewData) throws DxaException {

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
                                (thisKey.getControllerName().equals(viewData.getControllerName()) || viewData.getControllerName() == null);
                    }
                };
        Map<MvcData, Class<? extends ViewModel>> possibleValues = Maps.filterEntries(viewEntityClassMap, keyNamePredicate);
        if (possibleValues.isEmpty()) {
            //first let's see if there is another relevant view
            possibleValues = Maps.filterEntries(viewEntityClassMap, keyNamePredicateNoArea);
        }
        if (possibleValues.isEmpty()) {
            throw new DxaException(String.format("Could not find a view model for the view data %s", viewData));
        } else {
            return possibleValues.entrySet().iterator().next().getValue();
        }
    }

    @Override
    public Class<? extends ViewModel> getMappedModelTypes(String semanticTypeName) throws DxaException {

        Class<? extends ViewModel> retval = this.semanticMappingRegistry.getEntityClassByFullyQualifiedName(semanticTypeName);
        if (retval != null) {
            return retval;
        }
        //Fallback
        MvcData mvcData = new MvcDataImpl(semanticTypeName);
        return getViewModelType(mvcData);
    }

    @Override
    public Class<? extends ViewModel> getViewEntityClass(final String viewName) throws DxaException {

        final String areaName;
        final String scopedViewName;
        if (!viewName.contains(":")) { // Core module
            areaName = "Core";
            scopedViewName = viewName;
        } else {
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


        Map<MvcData, Class<? extends ViewModel>> possibleValues = Maps.filterEntries(viewEntityClassMap, keyNamePredicate);
        if (possibleValues.isEmpty()) {
            throw new DxaException(String.format("Could not find a view model for the view name %s", viewName));
        } else {
            return possibleValues.entrySet().iterator().next().getValue();
        }

    }
}

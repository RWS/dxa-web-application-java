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

package org.dd4t.databind.viewmodel.base;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.util.DataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * test
 *
 * @author R. Kempees
 */
public abstract class ViewModelBase implements BaseViewModel {
    private static final Logger LOG = LoggerFactory.getLogger(ViewModelBase.class);
    private final List<String> viewModelNames;
    private Map<String, Object> modelProperties;
    private boolean setRawDataOnModel;
    private Object rawData;

    /**
     * Default constructor. Leave this a no arg constructor!
     * <p/>
     * The constructor is fired at runtime and adds the associated
     * view model names on the List.
     */
    public ViewModelBase () {
        this.viewModelNames = new ArrayList<>();
        this.modelProperties = new HashMap<>();
        setGenericParameters();
        setFieldParameters();
    }

    protected void setGenericParameters () {
        LOG.trace("Setting generic parameters on model.");
        final ViewModel viewModelAnnotation = this.getClass().getAnnotation(ViewModel.class);
        if (null != viewModelAnnotation) {
            final List<String> rootElements = DataUtils.convertToNonEmptyList(viewModelAnnotation.rootElementNames());

            if (!rootElements.isEmpty()) {

                this.viewModelNames.addAll(rootElements);
            }
            final List<String> viewModels = DataUtils.convertToNonEmptyList(viewModelAnnotation.viewModelNames());
            if (!viewModels.isEmpty()) {
                this.viewModelNames.addAll(viewModels);
            }

            this.setRawDataOnModel = viewModelAnnotation.setRawData();
        }
    }

    private void setFieldParameters () {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field f : fields) {
            ViewModelProperty viewModelProperty = f.getAnnotation(ViewModelProperty.class);
            if (viewModelProperty != null) {
                String viewModelPropertyName;
                if (!StringUtils.isEmpty(viewModelProperty.entityFieldName())) {
                    viewModelPropertyName = viewModelProperty.entityFieldName();
                } else {
                    viewModelPropertyName = f.getName();
                }
                this.modelProperties.put(viewModelPropertyName, new ModelFieldMapping(viewModelProperty, f));
            }
        }
    }

    @Override
    public List<String> getViewNames () {
        return viewModelNames;
    }

    @Override
    public boolean setRawDataOnModel () {
        return setRawDataOnModel;
    }

    @Override
    public void setRawData (Object data) {
        this.rawData = data;
    }

    @Override
    public String getRawDataAsString () {
        if (this.rawData != null) {
            return (String) rawData;
        }
        return null;
    }


    @Override
    public Map<String, Object> getModelProperties () {
        return this.modelProperties;
    }
}

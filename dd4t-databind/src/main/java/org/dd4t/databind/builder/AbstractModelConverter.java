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
import org.dd4t.contentmodel.Embedded;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Multimedia;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.core.databind.TridionViewModel;
import org.dd4t.databind.util.TypeUtils;
import org.dd4t.databind.viewmodel.base.ModelFieldMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public abstract class AbstractModelConverter {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractModelConverter.class);

    protected static String getFieldKeyForModelProperty (final String fieldName, final ModelFieldMapping m) {
        final String fieldKey;
        if (m.getViewModelProperty() == null) {
            fieldKey = fieldName;
        } else {
            if (StringUtils.isEmpty(m.getViewModelProperty().entityFieldName())) {
                fieldKey = fieldName;
            } else {
                fieldKey = m.getViewModelProperty().entityFieldName();
            }
        }
        return fieldKey;
    }

    protected static <T extends BaseViewModel> void addToListTypeField (final T model, final Field modelField, final Object fieldValue) throws IllegalAccessException {
        List list = (List) modelField.get(model);
        if (list == null) {
            list = new ArrayList();
            list.add(fieldValue);
            modelField.set(model, list);
        } else {
            list.add(fieldValue);
        }
    }

    protected <T extends BaseViewModel> void setFieldValue (final T model, final Field f, final Object fieldValue, final FieldType fieldType) throws IllegalAccessException {

        boolean isMultiValued = false;
        Class<?> fieldTypeOfFieldToSet = TypeUtils.determineTypeOfField(f);
        if (f.getType().equals(List.class)) {
            isMultiValued = true;
        }

        if (fieldType == FieldType.EMBEDDED && (FieldSet.class.isAssignableFrom(fieldTypeOfFieldToSet) || Embedded.class.isAssignableFrom(fieldTypeOfFieldToSet))) {
            setEmbeddedFieldSetOnModelField(model, f, (org.dd4t.contentmodel.Field) fieldValue, isMultiValued);
        } else if (fieldValue instanceof org.dd4t.contentmodel.Field) {
            setFieldValueOnField(model, f, (org.dd4t.contentmodel.Field) fieldValue, isMultiValued);
        } else if (fieldValue instanceof Component) {
            setComponentOnField(model, f, (Component) fieldValue, isMultiValued);
        }
    }


    private <T extends BaseViewModel> void setEmbeddedFieldSetOnModelField (final T model, final Field f, final org.dd4t.contentmodel.Field fieldValue, final boolean isMultiValued) throws IllegalAccessException {
        LOG.debug("Setting Embedded Field on Model field");

        Object valueToSet;
        if (FieldSet.class.isAssignableFrom(f.getType())) {
            if (fieldValue.getValues() != null && !fieldValue.getValues().isEmpty()) {
                valueToSet = fieldValue.getValues().get(0);
            } else {
                valueToSet = null;
            }

        } else {
            valueToSet = fieldValue;
        }

        if (isMultiValued) {
            addToListTypeField(model, f, valueToSet);
        } else {
            f.set(model, valueToSet);
        }
    }

    private <T extends BaseViewModel> void setComponentOnField (final T model, final Field f, final Component fieldValue, final boolean isMultiValued) throws IllegalAccessException {
        LOG.debug("Setting component or multimedia on field");

        Object valueToSet = fieldValue;

        Class<?> modelPropertyType = TypeUtils.determineTypeOfField(f);

        if (Multimedia.class.isAssignableFrom(modelPropertyType)) {
            valueToSet = fieldValue.getMultimedia();
        }

        if (isMultiValued) {
            addToListTypeField(model, f, valueToSet);
        } else {
            f.set(model, valueToSet);
        }
    }

    private <T extends BaseViewModel> void setFieldValueOnField (final T model, final Field f, final org.dd4t.contentmodel.Field fieldValue, final boolean isMultiValued) throws IllegalAccessException {
        List<Object> values = fieldValue.getValues();

        if (values != null && !values.isEmpty()) {
            if (isMultiValued) {
                LOG.debug("Setting multivalued field: {}", f.getName());
                f.set(model, fieldValue.getValues());
            } else {
                f.set(model, fieldValue.getValues().get(0));
            }

            if (model instanceof TridionViewModel) {
                setXpm((TridionViewModel) model, fieldValue, isMultiValued);
            }

        } else {
            LOG.debug("No value(s) found!");
            if (model instanceof TridionViewModel) {
                setXpm((TridionViewModel) model, fieldValue, isMultiValued);
            }
        }
    }

    // TODO: only do when XPM enabled
    protected static void setXpm (final TridionViewModel model, final org.dd4t.contentmodel.Field renderedField, final boolean isMultiValued) {
        String xPath = renderedField.getXPath();
        model.addXpmEntry(renderedField.getName(), xPath, isMultiValued);
    }
}

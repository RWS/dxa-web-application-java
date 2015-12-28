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

package org.dd4t.databind.util;

import org.dd4t.core.databind.BaseViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class TypeUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TypeUtils.class);

    private TypeUtils () {

    }

    public static Type getRuntimeTypeOfTypeParameter (Type type) {
        if (type instanceof ParameterizedType) {
            Type[] genericTypes = ((ParameterizedType) type).getActualTypeArguments();
            if (genericTypes != null && genericTypes.length > 0) {
                LOG.debug("Type was List. Runtime Parametrized type is: {}", genericTypes[0].toString());
                return genericTypes[0];
            }
        }
        return Object.class;
    }

    public static boolean classIsViewModel (Class<?> clazz) {
        if (BaseViewModel.class.isAssignableFrom(clazz)) {
            LOG.debug("Current class is a View Model.");
            return true;
        }
        return false;
    }

    public static Class<?> determineTypeOfField (Field field) {
        if (field.getType().equals(List.class)) {
            return (Class<?>) TypeUtils.getRuntimeTypeOfTypeParameter(field.getGenericType());
        }
        return field.getType();
    }
}

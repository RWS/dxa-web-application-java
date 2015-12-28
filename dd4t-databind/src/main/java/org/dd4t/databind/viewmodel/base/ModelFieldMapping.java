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

import org.dd4t.databind.annotations.ViewModelProperty;

import java.lang.reflect.Field;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class ModelFieldMapping {
    private ViewModelProperty viewModelProperty;
    private Field field;

    public ModelFieldMapping (ViewModelProperty modelProperty, Field f) {
        this.field = f;
        this.viewModelProperty = modelProperty;
    }

    public ViewModelProperty getViewModelProperty () {
        return viewModelProperty;
    }

    public Field getField () {
        return field;
    }
}

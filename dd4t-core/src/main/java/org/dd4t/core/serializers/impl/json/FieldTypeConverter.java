/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
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

package org.dd4t.core.serializers.impl.json;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.FieldType;

/**
 * @author Mihai Cadariu
 * @since 10.07.2014
 */
public class FieldTypeConverter extends StdConverter<Object, FieldType> {

    @Override
    public FieldType convert (Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof FieldType) {
            return (FieldType) value;
        }

        if (value instanceof String) {
            String possibleTypeValue = (String) value;
            if (StringUtils.isNumeric(possibleTypeValue)) {
                return FieldType.findByValue(Integer.parseInt(possibleTypeValue));
            } else {
                return FieldType.findByName(possibleTypeValue);
            }
        }

        if (value instanceof Integer) {
            return FieldType.findByValue((int) value);
        }
        return null;
    }
}

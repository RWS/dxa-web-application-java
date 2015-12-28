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

package org.dd4t.contentmodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum FieldType {

    TEXT(0), MULTILINETEXT(1), XHTML(2), KEYWORD(3), EMBEDDED(4), MULTIMEDIALINK(5),
    COMPONENTLINK(6), EXTERNALLINK(7), NUMBER(8), DATE(9), UNKNOWN(-1);

    private static final Logger LOG = LoggerFactory.getLogger(FieldType.class);
    private final int value;


    FieldType (int value) {
        this.value = value;
    }

    public static FieldType findByValue (int value) {
        for (FieldType fieldType : values()) {
            if (fieldType.getValue() == value) {
                return fieldType;
            }
        }

        return UNKNOWN;
    }

    public static FieldType findByName (String name) {
        try {
            return FieldType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException iae) {
            LOG.error(iae.getLocalizedMessage(), iae);
            try {
                int value = Integer.parseInt(name);
                return findByValue(value);
            } catch (NumberFormatException nfe) {
                LOG.error(nfe.getLocalizedMessage(), nfe);
                return UNKNOWN;
            }
        }
    }

    public int getValue () {
        return value;
    }
}
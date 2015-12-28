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

package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.Embedded;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Schema;

import java.util.LinkedList;
import java.util.List;

public class EmbeddedField extends BaseField implements Field, Embedded {

    @JsonProperty ("EmbeddedSchema")
    @JsonDeserialize (as = SchemaImpl.class)
    private Schema embeddedSchema;

    public EmbeddedField () {
        setFieldType(FieldType.EMBEDDED);
    }

    @Override
    public List<Object> getValues () {
        List<Object> list = new LinkedList<>();

        for (FieldSet fs : getEmbeddedValues()) {
            list.add(fs);
        }

        return list;
    }

    @Override
    public Schema getEmbeddedSchema () {
        return embeddedSchema;
    }

    @Override
    public void setEmbeddedSchema (final Schema embeddedSchema) {
        this.embeddedSchema = embeddedSchema;
    }
}
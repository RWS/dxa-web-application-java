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

package org.dd4t.databind.serializers.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Map;

/**
 * Resolves Tridion Field Types to their proper concrete
 * classes, which are subclasses of the abstract BaseField class.
 * <p/>
 * This is done based on the "FieldType" parameter which
 * is sent by the DD4T templating assembly for each serialized Tridion Field.
 *
 * @author R. Kempees
 */
public class TridionFieldTypeIdResolver implements TypeIdResolver {

    private static final Logger LOG = LoggerFactory.getLogger(TridionFieldTypeIdResolver.class);
    private static final EnumMap<FieldType, String> FIELD_TYPES = new EnumMap<>(FieldType.class);
    private static final String NAMESPACE_PREFIX = "org.dd4t.contentmodel.impl.";
    private static final String TEXT_FIELD = "TextField";
    private static final String COMPONENT_LINK_FIELD = "ComponentLinkField";

    static {
        FIELD_TYPES.put(FieldType.TEXT, NAMESPACE_PREFIX + TEXT_FIELD);
        FIELD_TYPES.put(FieldType.MULTILINETEXT, NAMESPACE_PREFIX + TEXT_FIELD);
        FIELD_TYPES.put(FieldType.XHTML, NAMESPACE_PREFIX + "XhtmlField");
        FIELD_TYPES.put(FieldType.KEYWORD, NAMESPACE_PREFIX + "KeywordField");
        FIELD_TYPES.put(FieldType.EMBEDDED, NAMESPACE_PREFIX + "EmbeddedField");
        // This (5) is actually a MM Link field, but the links are needed for multi values and meta
        FIELD_TYPES.put(FieldType.MULTIMEDIALINK, NAMESPACE_PREFIX + COMPONENT_LINK_FIELD);
        FIELD_TYPES.put(FieldType.COMPONENTLINK, NAMESPACE_PREFIX + COMPONENT_LINK_FIELD);
        FIELD_TYPES.put(FieldType.EXTERNALLINK, NAMESPACE_PREFIX + TEXT_FIELD);
        FIELD_TYPES.put(FieldType.NUMBER, NAMESPACE_PREFIX + "NumericField");
        FIELD_TYPES.put(FieldType.DATE, NAMESPACE_PREFIX + "DateField");
        FIELD_TYPES.put(FieldType.UNKNOWN, NAMESPACE_PREFIX + "BaseField");
    }

    private JavaType mBaseType;

    public TridionFieldTypeIdResolver () {
    }


    @Override
    public void init (final JavaType javaType) {
        LOG.info("Instantiating TridionJsonFieldTypeResolver for " + javaType);
        mBaseType = javaType;
    }

    @Override
    public String idFromValue (final Object o) {
        return idFromValueAndType(o, o.getClass());
    }

    @Override
    public String idFromValueAndType (final Object o, final Class<?> aClass) {
        String name = aClass.getName();

        if (null == o) {
            return "-1";
        }

        return getIdFromClass(name);
    }

    @Override
    public String idFromBaseType () {
        return "-1";
    }

    @Override
    public JavaType typeFromId (final String s) {
        String clazzName = getClassForKey(s);
        Class<?> clazz;

        try {
            LOG.trace("Loading a '{}'", clazzName);
            clazz = TypeFactory.defaultInstance().findClass(clazzName);
        } catch (ClassNotFoundException e) {
            LOG.error("Could not find the class!", e);
            throw new IllegalStateException("Cannot find class '" + clazzName + "'");
        }

        return TypeFactory.defaultInstance().constructSpecializedType(mBaseType, clazz);
    }

    @Override
    public JavaType typeFromId (final DatabindContext databindContext, final String s) {
        return typeFromId(s);
    }

    @Override
    public JsonTypeInfo.Id getMechanism () {
        return JsonTypeInfo.Id.CUSTOM;
    }

    public static String getClassForKey (String type) {

        LOG.trace("Fetching field type for {}", type);
        FieldType fieldType;
        if (StringUtils.isNumeric(type)) {
            fieldType = FieldType.findByValue(Integer.parseInt(type));
        } else {
            fieldType = FieldType.findByName(type);
        }


        String result = FIELD_TYPES.get(fieldType);
        LOG.trace("Returning field type {}", result);

        return result;
    }

    private String getIdFromClass (String aClassName) {
        if (!aClassName.startsWith(NAMESPACE_PREFIX)) {
            aClassName = NAMESPACE_PREFIX + aClassName;
        }

        for (Map.Entry<FieldType, String> entry : FIELD_TYPES.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(aClassName)) {
                LOG.trace("Found {}::{}", entry.getKey(), entry.getValue());
                return entry.getKey().toString();
            }
        }

        return "-1";
    }
}

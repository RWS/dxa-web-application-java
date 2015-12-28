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

package org.dd4t.core.serializers.impl;

import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.serializers.Serializer;
import org.dd4t.core.serializers.impl.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * Factory class to allow easy access for deserialization / serialization
 * of DD4T Tridion Objects.
 * <p/>
 * Needed to hide implementations (JSON, XML)
 * and to have only one instance through the entire application.
 * <p/>
 * TODO: rework for databind
 *
 * @author R. Kempees
 * @since 06/06/14.
 */
public class SerializerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SerializerFactory.class);
    private static final SerializerFactory INSTANCE = new SerializerFactory();

    @Resource
    private Serializer serializer = null;

    private SerializerFactory () {
        LOG.debug("Init SerializerFactory.");
    }

    public SerializerFactory (final JSONSerializer serializerInstance) {
    }

    /**
     * This is not normally used, but for Unit Testing purposes.
     * <p/>
     * For normal use, use Spring injection.
     *
     * @param serializer
     */
    public static void setSerializer (org.dd4t.core.serializers.Serializer serializer) {
        if (INSTANCE != null) {
            INSTANCE.serializer = serializer;
        }
    }


    public static SerializerFactory getInstance () {
        return INSTANCE;
    }


    /**
     * Deserialize a Tridion DD4T content String.
     * <p/>
     * Uses the concrete implementation configured in the context configuration.
     *
     * @param content
     * @param aClass
     * @param <T>     Concrete Type of DD4T model object
     * @return Deserialized DD4T Model Object.
     * @throws SerializationException
     */
    public static <T> T deserialize (String content, Class<T> aClass) throws SerializationException {
        LOG.trace("Using Serializer: {}", INSTANCE.serializer.getClass());
        return INSTANCE.serializer.deserialize(content, aClass);
    }
}

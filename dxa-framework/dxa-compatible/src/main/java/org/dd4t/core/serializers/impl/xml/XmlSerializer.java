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

package org.dd4t.core.serializers.impl.xml;

import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.serializers.Serializer;


/**
 * XML (De)Serializer
 *
 * @author R. Kempees
 */
public class XmlSerializer implements Serializer {

    /**
     * TODO: implement XML version.
     *
     * @param content serialized content string coming from the DD4T providers
     * @param aClass  concrete type of desired deserialized class
     * @param <T>     The desired type to deserialize to
     * @return the deserialized object
     * @throws SerializationException
     */
    @Override
    public <T> T deserialize (final String content, final Class<T> aClass) throws SerializationException {
        return null;
    }

    /**
     * TODO: implement.
     *
     * @param item A serializable item
     * @return a serialized String representing the object
     * @throws SerializationException
     */
    @Override
    public String serialize (final Object item) throws SerializationException {
        return null;
    }
}

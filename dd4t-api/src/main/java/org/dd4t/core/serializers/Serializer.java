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

package org.dd4t.core.serializers;

import org.dd4t.core.exceptions.SerializationException;

/**
 * Serializer Interface. Use this to implement different
 * concrete Serializer or Databind classes and to hide the actual implementation.
 *
 * @author R. Kempees
 */
public interface Serializer {
    /**
     * @param content serialized content string coming from the DD4T providers
     * @param aClass  concrete type of desired deserialized class
     * @param <T>     The generic type of desired class
     * @return DD4T Tridion Model Object
     * @throws SerializationException
     */
    <T> T deserialize (String content, Class<T> aClass) throws SerializationException;

    /**
     * @param item the Object to Serialize
     * @return serialized string
     * @throws SerializationException
     */
    String serialize (Object item) throws SerializationException;
}

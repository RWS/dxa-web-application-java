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

import org.dd4t.contentmodel.Binary;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.contentmodel.impl.BinaryDataImpl;
import org.dd4t.contentmodel.impl.BinaryImpl;
import org.dd4t.core.util.CompressionUtils;
import org.dd4t.providers.transport.BinaryWrapper;

/**
 * Builds a Binary object from a BinaryWrapper object.
 *
 * @author Mihai Cadariu
 */
public class BinaryBuilder {

    /**
     * The BinaryWrapper contains both the Binary metadata and raw byte array content, but in encoded format.
     * The Binary meta will be Base64 decoded, then GZip decompressed, then JSON deserialized. Then the binary
     * byte array in the wrapper will be assigned into the decoded Binary.
     * The end result Binary contains both binary meta and the raw content byte array.
     *
     * @param wrapper BinaryWrapper to decode, decompress and convert to Binary
     * @return Binary a full Binary object containing metadata and raw content byte []
     * @throws SerializationException if anything goes wrong during decompressing, deserialization
     */
    public Binary build (BinaryWrapper wrapper) throws SerializationException {
        byte[] binaryBytes = CompressionUtils.decodeBase64(wrapper.getBinary());
        String binaryJSON = CompressionUtils.decompressGZip(binaryBytes);
        Binary result = SerializerFactory.deserialize(binaryJSON, BinaryImpl.class);

        BinaryDataImpl binaryData = new BinaryDataImpl();
        binaryData.setBytes(wrapper.getContent());
        result.setBinaryData(binaryData);

        return result;
    }
}

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

import org.dd4t.contentmodel.BinaryData;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by rai on 02/06/14.
 */
public class BinaryDataImpl implements BinaryData {

    private byte[] bytes;

    @Override
    public byte[] getBytes () {
        return this.bytes.clone();
    }

    public void setBytes (final byte[] bytes) {
        this.bytes = bytes.clone();
    }

    @Override
    public InputStream getInputStream () {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public long getDataSize () {
        return bytes == null ? 0 : bytes.length;
    }
}

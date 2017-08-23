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

package org.dd4t.providers.transport;

import java.io.Serializable;

/**
 * Java bean representing a wrapper for a Binary object and a binary content.
 * <p/>
 * The Binary is JSONed, then GZipped, then Base64 encoded. The binary content is a raw byte array.
 *
 * @author Mihai Cadariu
 */
public class BinaryWrapper implements Serializable {

    private String binary;
    private byte[] content;

    /**
     * Initialization constructor
     *
     * @param binary  String representing a Binary object that has been JSONed, GZipped and Base64 encoded
     * @param content byte array representing the raw content of a binary
     */
    public BinaryWrapper (String binary, byte[] content) {
        this.binary = binary;
        this.content = content.clone();
    }

    public byte[] getContent () {
        return content.clone();
    }

    public void setContent (byte[] content) {
        this.content = content.clone();
    }

    public String getBinary () {
        return binary;
    }

    public void setBinary (String binary) {
        this.binary = binary;
    }
}
